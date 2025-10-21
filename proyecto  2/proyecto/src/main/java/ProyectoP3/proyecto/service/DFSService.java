package ProyectoP3.proyecto.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class DFSService {

    private List<NodoEntity> mejorRuta = new ArrayList<>();
    private double mejorPeso = Double.MAX_VALUE;

    public List<NodoEntity> buscarRutaDFS(NodoEntity inicio, NodoEntity destino) {
        mejorRuta.clear();
        mejorPeso = Double.MAX_VALUE;
        dfs(inicio, destino, new HashSet<>(), new ArrayList<>(), 0);
        return mejorRuta;
    }

    private void dfs(NodoEntity actual, NodoEntity destino, Set<NodoEntity> visitados, List<NodoEntity> rutaActual, double pesoAcumulado) {
        if (actual.equals(destino)) {
            if (pesoAcumulado < mejorPeso) {
                mejorPeso = pesoAcumulado;
                mejorRuta = new ArrayList<>(rutaActual);
            }
            return;
        }

        visitados.add(actual);
        rutaActual.add(actual);

        for (RutaEntity ruta : actual.getRutas()) {
            NodoEntity siguiente = ruta.getDestino();
            if (!visitados.contains(siguiente)) {
                double nuevoPeso = pesoAcumulado + calcularPeso(ruta, siguiente.getUrgencia());
                dfs(siguiente, destino, visitados, rutaActual, nuevoPeso);
            }
        }

        rutaActual.remove(rutaActual.size() - 1); 
        visitados.remove(actual);
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
