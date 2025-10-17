package ProyectoP3.proyecto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;
import ProyectoP3.proyecto.service.BacktrackingService;
import ProyectoP3.proyecto.service.GreedyService;

@RestController
@RequestMapping("/rutas")
public class RutaController {

    private GreedyService greedyService = new GreedyService();
    private BacktrackingService backtrackingService = new BacktrackingService();

    // ===============================
    // 🔹 RUTA ENTRE DOS NODOS (Greedy)
    // ===============================
    @GetMapping("/greedy/{inicio}/{destino}")
    public List<String> rutaGreedy(@PathVariable String inicio, @PathVariable String destino) {
        Map<String, NodoEntity> mapa = crearMapa();
        inicio = inicio.trim();
        destino = destino.trim();

        NodoEntity nodoInicio = mapa.get(inicio);
        NodoEntity nodoDestino = mapa.get(destino);

        if (nodoInicio == null || nodoDestino == null) {
            throw new RuntimeException("Alguno de los nodos no existe en el mapa: " + inicio + " → " + destino);
        }

        List<NodoEntity> ruta = greedyService.encontrarRutaGreedy(nodoInicio, nodoDestino);
        List<String> nombres = new ArrayList<>();
        for (NodoEntity n : ruta) {
            if (n != null) nombres.add(n.getNombre());
        }
        return nombres;
    }

    // ===============================
    // 🔹 RUTA ENTRE DOS NODOS (Backtracking)
    // ===============================
    @GetMapping("/backtracking/{inicio}/{destino}")
    public List<String> rutaBacktracking(@PathVariable String inicio, @PathVariable String destino) {
        Map<String, NodoEntity> mapa = crearMapa();
        inicio = inicio.trim();
        destino = destino.trim();

        NodoEntity nodoInicio = mapa.get(inicio);
        NodoEntity nodoDestino = mapa.get(destino);

        if (nodoInicio == null || nodoDestino == null) {
            throw new RuntimeException("Alguno de los nodos no existe en el mapa: " + inicio + " → " + destino);
        }

        List<NodoEntity> ruta = backtrackingService.encontrarRutaOptima(nodoInicio, nodoDestino);
        List<String> nombres = new ArrayList<>();
        for (NodoEntity n : ruta) {
            if (n != null) nombres.add(n.getNombre());
        }
        return nombres;
    }

    // =========================================
    // 🔹 NUEVO: RUTA COMPLETA DEL DRON (recorrido)
    // =========================================
    @GetMapping("/dron")
    public Map<String, Object> rutaDelDron() {
        Map<String, NodoEntity> mapa = crearMapa();
        List<String> recorrido = new ArrayList<>();

        // El dron empieza en el centro de distribución
        NodoEntity actual = mapa.get("Base Central");
        recorrido.add("🚁 Dron despega desde " + actual.getNombre());

        // Secuencia de entrega (puede variarse)
        String[] paradas = {
            "Hospital Norte",
            "Barrio El Progreso",
            "Barrio Los Álamos",
            "Barrio Esperanza",
            "Punto Recarga 1",
            "Hospital Sur",
            "Barrio Las Rosas"
        };

        for (String destino : paradas) {
            NodoEntity nodoDestino = mapa.get(destino);
            if (nodoDestino != null) {
                recorrido.add("➡️ Vuelo hacia " + nodoDestino.getNombre() + " (" + nodoDestino.getTipo() + ")");
                recorrido.add("Entrega completada en " + nodoDestino.getNombre() + " ✅");
                actual = nodoDestino;
            }
        }

        recorrido.add("🏁 Dron regresa al Centro de Distribución");
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ruta_dron", recorrido);
        respuesta.put("nodos_visitados", paradas.length + 1);
        return respuesta;
    }

    // =========================================
    // 🔹 GRAFO COMPLETO DE NODOS Y CONEXIONES
    // =========================================
    private Map<String, NodoEntity> crearMapa() {

        // --- NODOS PRINCIPALES ---
        NodoEntity base = new NodoEntity("Base Central", "CentroDistribucion", 1, 0);
        NodoEntity hospitalN = new NodoEntity("Hospital Norte", "Hospital", 4, 120);
        NodoEntity hospitalS = new NodoEntity("Hospital Sur", "Hospital", 3, 80);
        NodoEntity zona1 = new NodoEntity("Barrio El Progreso", "ZonaAfectada", 5, 200);
        NodoEntity zona2 = new NodoEntity("Barrio Los Álamos", "ZonaAfectada", 4, 150);
        NodoEntity zona3 = new NodoEntity("Barrio Esperanza", "ZonaAfectada", 5, 180);
        NodoEntity zona4 = new NodoEntity("Barrio Las Rosas", "ZonaAfectada", 3, 90);
        NodoEntity recarga1 = new NodoEntity("Punto Recarga 1", "CentroRecarga", 2, 0);

        // --- RUTAS (pesos combinados según clima, energía, tiempo, obstáculos) ---
        RutaEntity r1 = new RutaEntity(10, 15, "Despejado", 0.1, hospitalN);
        RutaEntity r2 = new RutaEntity(20, 30, "Lluvia", 0.3, zona1);
        RutaEntity r3 = new RutaEntity(12, 18, "Viento", 0.2, zona2);
        RutaEntity r4 = new RutaEntity(25, 35, "Tormenta", 0.5, zona3);
        RutaEntity r5 = new RutaEntity(8, 12, "Despejado", 0.1, recarga1);
        RutaEntity r6 = new RutaEntity(10, 15, "Despejado", 0.1, hospitalS);
        RutaEntity r7 = new RutaEntity(15, 20, "Lluvia", 0.2, zona4);

        // --- CONEXIONES ---
        base.getRutas().add(r1);
        base.getRutas().add(r2);
        base.getRutas().add(r3);
        base.getRutas().add(r5);
        hospitalN.getRutas().add(r4);
        zona1.getRutas().add(r5);
        recarga1.getRutas().add(r6);
        hospitalS.getRutas().add(r7);

        // --- MAPA COMPLETO ---
        Map<String, NodoEntity> mapa = new HashMap<>();
        mapa.put("Base Central", base);
        mapa.put("Hospital Norte", hospitalN);
        mapa.put("Hospital Sur", hospitalS);
        mapa.put("Barrio El Progreso", zona1);
        mapa.put("Barrio Los Álamos", zona2);
        mapa.put("Barrio Esperanza", zona3);
        mapa.put("Barrio Las Rosas", zona4);
        mapa.put("Punto Recarga 1", recarga1);

        return mapa;
    }
}
