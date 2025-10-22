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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;
import ProyectoP3.proyecto.model.RutaRequest;
import ProyectoP3.proyecto.repo.NodoRepository;
import ProyectoP3.proyecto.service.BacktrackingService;
import ProyectoP3.proyecto.service.DFSService;
import ProyectoP3.proyecto.service.DijkstraService;
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
    @Autowired
    private ProgramacionDinamicaService programacionDinamicaService;

    @GetMapping("/greedy/{inicio}/{destino}")
    public List<String> rutaGreedy(@PathVariable String inicio, @PathVariable String destino) {
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();
        if (nodoInicio == null || nodoDestino == null) throw new RuntimeException("Alguno de los nodos no existe: " + inicio + " → " + destino);
        List<NodoEntity> ruta = greedyService.encontrarRutaGreedy(nodoInicio, nodoDestino);
        List<String> nombres = new ArrayList<>();
        for (NodoEntity n : ruta) if (n != null) nombres.add(n.getNombre());
        return nombres;
    }

    @GetMapping("/backtracking/{inicio}/{destino}")
    public List<String> rutaBacktracking(@PathVariable String inicio, @PathVariable String destino) {
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();
        if (nodoInicio == null || nodoDestino == null) throw new RuntimeException("Alguno de los nodos no existe: " + inicio + " → " + destino);
        List<NodoEntity> ruta = backtrackingService.encontrarRutaOptima(nodoInicio, nodoDestino);
        return ruta.stream().map(NodoEntity::getNombre).toList();
    }

    @GetMapping("/dfs/{inicio}/{destino}")
    public List<String> rutaDFS(@PathVariable String inicio, @PathVariable String destino) {
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();
        if (nodoInicio == null || nodoDestino == null) throw new RuntimeException("Alguno de los nodos no existe: " + inicio + " → " + destino);
        List<NodoEntity> ruta = dfsService.buscarRutaDFS(nodoInicio, nodoDestino);
        return ruta.stream().map(NodoEntity::getNombre).toList();
    }

    @GetMapping("/poda/{inicio}/{destino}")
    public List<String> rutaPoda(@PathVariable String inicio, @PathVariable String destino) {
        NodoEntity nodoInicio = nodoRepository.findByNombre(inicio.trim()).block();
        NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();
        if (nodoInicio == null || nodoDestino == null) throw new RuntimeException("Alguno de los nodos no existe: " + inicio + " → " + destino);
        List<NodoEntity> ruta = ramificacionPodaService.buscarRutaOptima(nodoInicio, nodoDestino);
        return ruta.stream().map(NodoEntity::getNombre).toList();
    }

    @GetMapping("/divide")
public Map<String, Object> rutaDividida(@RequestParam List<String> nodos) {
    List<NodoEntity> rutaSecuencial = new ArrayList<>();

    for (String nombre : nodos) {
        NodoEntity nodo = nodoRepository.findByNombre(nombre.trim()).block();
        if (nodo == null) {
            throw new RuntimeException("⚠️ Nodo no encontrado: " + nombre);
        }
        rutaSecuencial.add(nodo);
    }

    double costoTotal = divideService.resolverRutaDividida(rutaSecuencial);

    Map<String, Object> respuesta = new HashMap<>();
    respuesta.put("ruta", nodos);
    respuesta.put("costoTotal", Math.round(costoTotal * 100.0) / 100.0); // redondeado a 2 decimales
    return respuesta;
}




    public double rutaDividida() {
        List<NodoEntity> rutaSecuencial = List.of(
            nodoRepository.findByNombre("Base Central").block(),
            nodoRepository.findByNombre("Hospital Norte").block(),
            nodoRepository.findByNombre("Punto Recarga 1").block(),
            nodoRepository.findByNombre("Barrio El Progreso").block()
        );
        return divideService.resolverRutaDividida(rutaSecuencial);
    }

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

    @PostMapping("/crear-ruta")
    public void crearRuta(@RequestBody RutaRequest request) {
        NodoEntity origen = nodoRepository.findByNombre(request.getOrigen()).block();
        NodoEntity destino = nodoRepository.findByNombre(request.getDestino()).block();
        if (origen == null || destino == null) throw new RuntimeException("Origen o destino no encontrado");
        RutaEntity ruta = new RutaEntity(request.getTiempo(), request.getEnergia(), request.getClima(), request.getObstaculos(), destino);
        origen.getRutas().add(ruta);
        nodoRepository.save(origen).block();
    }

    @PostMapping("/nodos/crear")
    public void crearNodo(@RequestBody NodoEntity nodo) {
        nodoRepository.save(nodo).block();
    }

    @GetMapping("/dron")
    public Map<String, Object> rutaDelDron() {
        List<String> recorrido = new ArrayList<>();
        NodoEntity actual = nodoRepository.findByNombre("Base Central").block();
        recorrido.add("🚁 Dron despega desde " + actual.getNombre());
        String[] paradas = {"Hospital Norte","Barrio El Progreso","Barrio Los Álamos","Barrio Esperanza","Punto Recarga 1","Hospital Sur","Barrio Las Rosas"};
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

    @Autowired
private DijkstraService dijkstraService;

@GetMapping("/dijkstra/{origen}/{destino}")
public List<String> rutaDijkstra(
        @PathVariable String origen,
        @PathVariable String destino,
        @RequestParam(required = false) String tipoPeso) {

    NodoEntity nodoInicio = nodoRepository.findByNombre(origen.trim()).block();
    NodoEntity nodoDestino = nodoRepository.findByNombre(destino.trim()).block();

    if (nodoInicio == null || nodoDestino == null) {
        throw new RuntimeException("Origen o destino no encontrado");
    }

    if (tipoPeso != null && !tipoPeso.isEmpty()) {
        dijkstraService.setTipoPeso(tipoPeso);
    }

    List<NodoEntity> ruta = dijkstraService.calcularRutaMinima(nodoInicio, nodoDestino);

    List<String> nombres = new ArrayList<>();
    for (NodoEntity n : ruta) {
        if (n != null) nombres.add(n.getNombre());
    }
    return nombres;
}

}
