package ProyectoP3.proyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class ProgramacionDinamicaService {

    public double resolverRutaDinamica(List<NodoEntity> rutaSecuencial) {
        if (rutaSecuencial == null || rutaSecuencial.size() < 2) return 0;

        double[] dp = new double[rutaSecuencial.size()];
        dp[0] = 0; // el primer nodo no tiene costo

        for (int i = 1; i < rutaSecuencial.size(); i++) {
            NodoEntity anterior = rutaSecuencial.get(i - 1);
            NodoEntity actual = rutaSecuencial.get(i);

            RutaEntity ruta = buscarRuta(anterior, actual);
            if (ruta != null) {
                double peso = calcularPeso(ruta, actual.getUrgencia());
                dp[i] = dp[i - 1] + peso;
            } else {
                dp[i] = dp[i - 1] + 1000; // penalización si no hay conexión directa
            }
        }

        return dp[rutaSecuencial.size() - 1];
    }

    private RutaEntity buscarRuta(NodoEntity origen, NodoEntity destino) {
        if (origen.getRutas() == null) return null;
        for (RutaEntity r : origen.getRutas()) {
            if (r.getDestino().getNombre().equalsIgnoreCase(destino.getNombre())) {
                return r;
            }
        }
        return null;
    }

    private double calcularPeso(RutaEntity r, int urgencia) {
        double clima = 0;
        if (r.getClima() != null) {
            if (r.getClima().equalsIgnoreCase("Viento")) clima = 0.2;
            else if (r.getClima().equalsIgnoreCase("Lluvia")) clima = 0.4;
            else if (r.getClima().equalsIgnoreCase("Tormenta")) clima = 0.7;
        }
        return (r.getTiempo() * 0.3) + (r.getEnergia() * 0.3) + (r.getObstaculos() * 0.2) + (urgencia * 0.2) + clima;
    }
}
