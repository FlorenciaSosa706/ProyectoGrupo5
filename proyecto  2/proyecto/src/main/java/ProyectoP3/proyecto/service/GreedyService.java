package ProyectoP3.proyecto.service;

import java.util.*;
import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

public class GreedyService {

    public List<NodoEntity> encontrarRutaGreedy(NodoEntity inicio, NodoEntity destino) {
        Set<NodoEntity> visitados = new HashSet<>();
        List<NodoEntity> ruta = new ArrayList<>();
        NodoEntity actual = inicio;

        while (actual != null && !actual.equals(destino)) {
            ruta.add(actual);
            visitados.add(actual);

            RutaEntity mejorRuta = actual.getRutas().stream()
                .filter(r -> !visitados.contains(r.getDestino()))
                .min(Comparator.comparingDouble(r -> calcularPeso(r, r.getDestino().getUrgencia())))
                .orElse(null);

            if (mejorRuta == null) break;
            actual = mejorRuta.getDestino();
        }

        ruta.add(destino);
        return ruta;
    }

    private double calcularPeso(RutaEntity r, int urgencia) {
        double climaFactor = 0.0;
        if(r.getClima().equalsIgnoreCase("Viento")) climaFactor = 0.2;
        if(r.getClima().equalsIgnoreCase("Lluvia")) climaFactor = 0.4;
        if(r.getClima().equalsIgnoreCase("Tormenta")) climaFactor = 0.7;

        return (r.getTiempo() * 0.3) + (r.getEnergia() * 0.3) + (r.getObstaculos() * 0.2) + (urgencia * 0.2) + climaFactor;
    }
}
