package ProyectoP3.proyecto.service;

import java.util.*;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;
import org.springframework.stereotype.Service;

@Service
public class DijkstraService {

    // Tipo de peso: "tiempo", "energia", "obstaculos" o cualquier combinación
    private String tipoPeso = "tiempo"; // HAY QUE DECIDIRSE CON EL TIPO DE PESO, PERO SE PUEDE CAMBIAR.

    public void setTipoPeso(String tipoPeso) {
        this.tipoPeso = tipoPeso;
    }

    public List<NodoEntity> calcularRutaMinima(NodoEntity origen, NodoEntity destino) {
        Map<NodoEntity, Double> distancias = new HashMap<>();
        Map<NodoEntity, NodoEntity> predecesores = new HashMap<>();
        Set<NodoEntity> visitados = new HashSet<>();
        PriorityQueue<NodoEntity> cola = new PriorityQueue<>(Comparator.comparingDouble(distancias::get));

        distancias.put(origen, 0.0);
        cola.add(origen);

        while (!cola.isEmpty()) {
            NodoEntity actual = cola.poll();
            visitados.add(actual);

            if (actual.equals(destino)) break;

            for (RutaEntity ruta : actual.getRutas()) {
                NodoEntity vecino = ruta.getDestino();
                if (visitados.contains(vecino)) continue;

                double peso = getPesoRuta(ruta); // calcula el peso según tipoPeso
                double nuevaDistancia = distancias.getOrDefault(actual, Double.MAX_VALUE) + peso;

                if (nuevaDistancia < distancias.getOrDefault(vecino, Double.MAX_VALUE)) {
                    distancias.put(vecino, nuevaDistancia);
                    predecesores.put(vecino, actual);
                    cola.add(vecino);
                }
            }
        }

        // Reconstruir camino
        List<NodoEntity> camino = new LinkedList<>();
        NodoEntity paso = destino;
        while (paso != null) {
            camino.add(0, paso);
            paso = predecesores.get(paso);
        }

        return camino;
    }

    // Determina el "peso" de la ruta según la variable tipoPeso
    private double getPesoRuta(RutaEntity ruta) {
        switch (tipoPeso.toLowerCase()) {
            case "energia": return ruta.getEnergia();
            case "obstaculos": return ruta.getObstaculos();
            case "tiempo":
            default:
                return ruta.getTiempo();
        }
    }
}
