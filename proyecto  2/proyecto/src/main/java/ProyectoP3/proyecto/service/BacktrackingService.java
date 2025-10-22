package ProyectoP3.proyecto.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class BacktrackingService {

    private double mejorPeso = Double.MAX_VALUE;
    private List<NodoEntity> mejorRuta = new ArrayList<>();

    public List<NodoEntity> encontrarRutaOptima(NodoEntity inicio, NodoEntity destino) {
        mejorPeso = Double.MAX_VALUE;
        mejorRuta.clear();
        Set<String> visitados = new HashSet<>();
        List<NodoEntity> rutaActual = new ArrayList<>();
        backtracking(inicio, destino, visitados, rutaActual, 0);
        return mejorRuta;
    }

    private void backtracking(NodoEntity actual, NodoEntity destino, Set<String> visitados, List<NodoEntity> rutaActual, double peso) {
        if(actual == null) return;

        if(actual.getNombre().equalsIgnoreCase(destino.getNombre())) {
            rutaActual.add(actual);
            if(peso < mejorPeso) {
                mejorPeso = peso;
                mejorRuta = new ArrayList<>(rutaActual);
            }
            rutaActual.remove(rutaActual.size()-1);
            return;
        }

        visitados.add(actual.getNombre());
        rutaActual.add(actual);

        if(actual.getRutas() != null) {
            for(RutaEntity r : actual.getRutas()) {
                NodoEntity sig = r.getDestino();
                if(sig != null && !visitados.contains(sig.getNombre())) {
                    double nuevoPeso = peso + calcularPeso(r, sig.getUrgencia());
                    backtracking(sig, destino, visitados, rutaActual, nuevoPeso);
                }
            }
        }

        rutaActual.remove(rutaActual.size()-1);
        visitados.remove(actual.getNombre());
    }

    private double calcularPeso(RutaEntity r, int urgencia) {
        double clima = 0;
        if(r.getClima() != null) {
            if(r.getClima().equalsIgnoreCase("Viento")) clima = 0.2;
            else if(r.getClima().equalsIgnoreCase("Lluvia")) clima = 0.4;
            else if(r.getClima().equalsIgnoreCase("Tormenta")) clima = 0.7;
        }
        return (r.getTiempo() * 0.3) + (r.getEnergia() * 0.3) + (r.getObstaculos() * 0.2) + (urgencia * 0.2) + clima;
    }
}
