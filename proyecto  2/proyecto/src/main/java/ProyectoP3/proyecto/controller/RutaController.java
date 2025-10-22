package ProyectoP3.proyecto.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;
import ProyectoP3.proyecto.model.RutaRequest;
import ProyectoP3.proyecto.repo.NodoRepository;
import ProyectoP3.proyecto.service.BacktrackingService;
import ProyectoP3.proyecto.service.DFSService;
import ProyectoP3.proyecto.service.DivideVencerasService;
import ProyectoP3.proyecto.service.GreedyService;
import ProyectoP3.proyecto.service.ProgramacionDinamicaService;
import ProyectoP3.proyecto.service.RamificacionPodaService;

@RestController
@RequestMapping("/rutas")
public class RutaController {

    @Autowired
    private NodoRepository nodoRepository;
    

    @Autowired
    private GreedyService greedyService;

    @Autowired
    private BacktrackingService backtrackingService;

    @Autowired
    private DFSService dfsService;

    @Autowired
    private RamificacionPodaService ramificacionPodaService;

    @Autowired
    private DivideVencerasService divideService;

    



    // ===============================
    // 🔹 RUTA ENTRE DOS NODOS (Greedy)
    // ===============================
    @GetMapping("/greedy/{inicio}/{destino}")
    public List<String> rutaGreedy(@PathVariable String inicio, @PathVariable String destino) {
        
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();

        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();

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
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();

        if (nodoInicio == null || nodoDestino == null) {
            throw new RuntimeException("Alguno de los nodos no existe en Neo4j: " + inicio + " → " + destino);
        }

        List<NodoEntity> ruta = backtrackingService.encontrarRutaOptima(nodoInicio, nodoDestino);
        return ruta.stream().map(NodoEntity::getNombre).toList();
    }

    /////AGREGO ALGORITMO DFS //////
    @GetMapping("/dfs/{inicio}/{destino}")
    public List<String> rutaDFS(@PathVariable String inicio, @PathVariable String destino) {
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();

        if (nodoInicio == null || nodoDestino == null) {
            throw new RuntimeException("Alguno de los nodos no existe en Neo4j: " + inicio + " → " + destino);
        }

        List<NodoEntity> ruta = dfsService.buscarRutaDFS(nodoInicio, nodoDestino);
        return ruta.stream().map(NodoEntity::getNombre).toList();
    }


    //AGREGO ALGORITMO DE RAMIFICACION Y PODA///////
    @GetMapping("/poda/{inicio}/{destino}")
    public List<String> rutaPoda(@PathVariable String inicio, @PathVariable String destino) {
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();

        if (nodoInicio == null || nodoDestino == null) {
            throw new RuntimeException("Alguno de los nodos no existe en Neo4j: " + inicio + " → " + destino);
        }

        List<NodoEntity> ruta = ramificacionPodaService.buscarRutaOptima(nodoInicio, nodoDestino);
        return ruta.stream().map(NodoEntity::getNombre).toList();
}


    /////////AGREGO ALGORITMO DE DIVIDE Y VENCERÁS////////
    @GetMapping("/divide")
    public double rutaDividida() {
        List<NodoEntity> rutaSecuencial = List.of(
            nodoRepository.findByNombre("Base Central").block(),
            nodoRepository.findByNombre("Hospital Norte").block(),
            nodoRepository.findByNombre("Punto Recarga 1").block(),
            nodoRepository.findByNombre("Barrio El Progreso").block()
        );

        return divideService.resolverRutaDividida(rutaSecuencial);
    }


    @PostMapping("/crear-ruta")
    public void crearRuta(@RequestBody RutaRequest request) {
        NodoEntity origen = nodoRepository.findByNombre(request.getOrigen()).block();
        NodoEntity destino = nodoRepository.findByNombre(request.getDestino()).block();

        if (origen == null || destino == null) {
            throw new RuntimeException("Origen o destino no encontrado");
        }

        RutaEntity ruta = new RutaEntity(
            request.getTiempo(),
            request.getEnergia(),
            request.getClima(),
            request.getObstaculos(),
            destino
        );

    origen.getRutas().add(ruta);
    nodoRepository.save(origen).block();
}






    // =========================================
    // 🔹 NUEVO: RUTA COMPLETA DEL DRON (recorrido)
    // =========================================
    @GetMapping("/dron")
    public Map<String, Object> rutaDelDron() {
        List<String> recorrido = new ArrayList<>();
        NodoEntity actual = nodoRepository.findByNombre("Base Central").block();
        recorrido.add("🚁 Dron despega desde " + actual.getNombre());

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
            NodoEntity nodoDestino = nodoRepository.findByNombre(destino).block();
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
    @PostMapping("/nodos/crear")
    public void crearNodo(@RequestBody NodoEntity nodo) {
        nodoRepository.save(nodo).block();
    }   

    @Autowired
private ProgramacionDinamicaService programacionDinamicaService;

@GetMapping("/dinamica")
public double rutaDinamica() {
    List<NodoEntity> rutaSecuencial = List.of(
        nodoRepository.findByNombre("Base Central").block(),
        nodoRepository.findByNombre("Hospital Norte").block(),
        nodoRepository.findByNombre("Punto Recarga 1").block(),
        nodoRepository.findByNombre("Barrio El Progreso").block()
    );

    return programacionDinamicaService.resolverRutaDinamica(rutaSecuencial);
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
