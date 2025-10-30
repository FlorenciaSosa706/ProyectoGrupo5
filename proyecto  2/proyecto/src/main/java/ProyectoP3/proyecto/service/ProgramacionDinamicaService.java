package ProyectoP3.proyecto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class ProgramacionDinamicaService {

    private final PesoService pesoService;

    @Autowired
    public ProgramacionDinamicaService(PesoService pesoService) {
        this.pesoService = pesoService;
    }
    
    public double resolverRutaDinamica(List<NodoEntity> rutaSecuencial) {
        if (rutaSecuencial == null || rutaSecuencial.size() < 2) return 0;

        double[] dp = new double[rutaSecuencial.size()];
        dp[0] = 0; // el primer nodo no tiene costo

        for (int i = 1; i < rutaSecuencial.size(); i++) {
            NodoEntity anterior = rutaSecuencial.get(i - 1);
            NodoEntity actual = rutaSecuencial.get(i);

            RutaEntity ruta = buscarRuta(anterior, actual);
            if (ruta != null) {
                double peso = pesoService.calcularPeso(ruta, actual.getUrgencia());
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
}
