
package ProyectoP3.proyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class DivideVencerasService {

    /**
     * Divide el problema en pares de nodos consecutivos y calcula el peso de cada tramo.
     * No usa DFS ni backtracking, solo calcula directamente los pesos entre nodos conectados.
     */
    public double resolverRutaDividida(List<NodoEntity> rutaSecuencial) {
        double pesoTotal = 0.0;

        for (int i = 0; i < rutaSecuencial.size() - 1; i++) {
            NodoEntity origen = rutaSecuencial.get(i);
            NodoEntity destino = rutaSecuencial.get(i + 1);

            RutaEntity ruta = encontrarRutaDirecta(origen, destino);
            if (ruta != null) {
                pesoTotal += calcularPeso(ruta, destino.getUrgencia());
            } else {
                // Si no hay conexión directa, se penaliza o se lanza excepción
                pesoTotal += 1000; // penalización arbitraria
            }
        }

        return pesoTotal;
    }

    private RutaEntity encontrarRutaDirecta(NodoEntity origen, NodoEntity destino) {
        for (RutaEntity r : origen.getRutas()) {
            if (r.getDestino().equals(destino)) {
                return r;
            }
        }
        return null;
    }

    private double calcularPeso(RutaEntity r, int urgencia) {
        double climaFactor = switch (r.getClima().toLowerCase()) {
            case "viento" -> 0.2;
            case "lluvia" -> 0.4;
            case "tormenta" -> 0.7;
            default -> 0.0;
        };

        return (r.getTiempo() * 0.3) + (r.getEnergia() * 0.3) + (r.getObstaculos() * 0.2) + (urgencia * 0.2) + climaFactor;
    }
}
