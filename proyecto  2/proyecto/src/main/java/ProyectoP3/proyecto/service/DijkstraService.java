package ProyectoP3.proyecto.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class DijkstraService {

    private final PesoService pesoService;

    @Autowired
    public DijkstraService(PesoService pesoService) {
        this.pesoService = pesoService;
    }

    public List<NodoEntity> calcularRutaMinima(NodoEntity origen, NodoEntity destino) {
        Map<String, Double> distancias = new HashMap<>();
        Map<String, NodoEntity> predecesores = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        distancias.put(origen.getNombre(), 0.0);

        PriorityQueue<NodoEntity> cola = new PriorityQueue<>(
            Comparator.comparingDouble(n -> distancias.getOrDefault(n.getNombre(), Double.MAX_VALUE))
        );
        cola.add(origen);

        while (!cola.isEmpty()) {
            NodoEntity actual = cola.poll();
            visitados.add(actual.getNombre());

            if (actual.getNombre().equals(destino.getNombre())) break;

            if (actual.getRutas() != null) {
                for (RutaEntity r : actual.getRutas()) {
                    NodoEntity vecino = r.getDestino();
                    if (vecino == null || visitados.contains(vecino.getNombre())) continue;

                    double peso = pesoService.calcularPeso(r, vecino.getUrgencia());
                    double nuevaDistancia = distancias.getOrDefault(actual.getNombre(), Double.MAX_VALUE) + peso;

                    if (nuevaDistancia < distancias.getOrDefault(vecino.getNombre(), Double.MAX_VALUE)) {
                        distancias.put(vecino.getNombre(), nuevaDistancia);
                        predecesores.put(vecino.getNombre(), actual);
                        cola.add(vecino);
                    }
                }
            }
        }

        List<NodoEntity> camino = new LinkedList<>();
        NodoEntity paso = destino;
        while (paso != null) {
            camino.add(0, paso);
            paso = predecesores.get(paso.getNombre());
        }

        if (camino.isEmpty() || !camino.get(0).getNombre().equals(origen.getNombre())) {
            return Collections.emptyList();
        }

        return camino;
    }
}
