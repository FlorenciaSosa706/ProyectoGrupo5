package ProyectoP3.proyecto.service;

import java.util.*;
import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

public class BacktrackingService {

    private double mejorPeso = Double.MAX_VALUE;
    private List<NodoEntity> mejorRuta = new ArrayList<>();

    public List<NodoEntity> encontrarRutaOptima(NodoEntity inicio, NodoEntity destino) {
        mejorPeso = Double.MAX_VALUE;
        mejorRuta.clear();
        backtrack(inicio, destino, new HashSet<>(), new ArrayList<>(), 0);
        return mejorRuta;
    }

    private void backtrack(NodoEntity actual, NodoEntity destino, Set<NodoEntity> visitados, List<NodoEntity> rutaActual, double pesoAcumulado) {
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
            NodoEntity sig = ruta.getDestino();
            if (!visitados.contains(sig)) {
                double nuevoPeso = pesoAcumulado + calcularPeso(ruta, sig.getUrgencia());
                backtrack(sig, destino, visitados, rutaActual, nuevoPeso);
            }
        }

        rutaActual.remove(rutaActual.size() - 1);
        visitados.remove(actual);
    }

    private double calcularPeso(RutaEntity r, int urgencia) {
        double climaFactor = 0.0;
        if(r.getClima().equalsIgnoreCase("Viento")) climaFactor = 0.2;
        if(r.getClima().equalsIgnoreCase("Lluvia")) climaFactor = 0.4;
        if(r.getClima().equalsIgnoreCase("Tormenta")) climaFactor = 0.7;

        return (r.getTiempo() * 0.3) + (r.getEnergia() * 0.3) + (r.getObstaculos() * 0.2) + (urgencia * 0.2) + climaFactor;
    }
}
