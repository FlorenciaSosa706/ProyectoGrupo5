package ProyectoP3.proyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class DivideVencerasService {

    public double resolverRutaDividida(List<NodoEntity> rutaSecuencial) {
        double pesoTotal = 0;

        for(int i = 0; i < rutaSecuencial.size() - 1; i++) {
            NodoEntity origen = rutaSecuencial.get(i);
            NodoEntity destino = rutaSecuencial.get(i + 1);

            RutaEntity ruta = buscarRutaDirecta(origen, destino);

            if(ruta != null) {
                pesoTotal += calcularPeso(ruta, destino.getUrgencia());
            } else {
                // si no hay conexión directa, le sumo un peso grande como penalización
                pesoTotal += 1000;
            }
        }

        return pesoTotal;
    }

    private RutaEntity buscarRutaDirecta(NodoEntity origen, NodoEntity destino) {
        if(origen.getRutas() == null) return null;
        for(RutaEntity r : origen.getRutas()) {
            if(r.getDestino().getNombre().equalsIgnoreCase(destino.getNombre())) {
                return r;
            }
        }
        return null;
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