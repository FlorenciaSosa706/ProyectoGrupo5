package ProyectoP3.proyecto.service;

import ProyectoP3.proyecto.model.RutaEntity;

import org.springframework.stereotype.Service;
@Service
public class PesoService {

    public double calcularPeso(RutaEntity r, int urgencia) {
        double clima = 0;
        if (r.getClima() != null) {
            switch (r.getClima().toLowerCase()) {
                case "viento" -> clima = 0.2;
                case "lluvia" -> clima = 0.4;
                case "tormenta" -> clima = 0.7;
            }
        }
        return (r.getTiempo() * 0.3)
             + (r.getEnergia() * 0.3)
             + (r.getObstaculos() * 0.2)
             + (urgencia * 0.2)
             + clima;
    }
}
