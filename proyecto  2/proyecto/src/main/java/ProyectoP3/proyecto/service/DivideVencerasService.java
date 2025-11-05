package ProyectoP3.proyecto.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class DivideVencerasService {

    private final PesoService pesoService;

    //@Autowired
    public DivideVencerasService(PesoService pesoService) {
        this.pesoService = pesoService;
    }

    public double resolverRutaDividida(List<NodoEntity> rutaSecuencial) {
        double pesoTotal = 0;

        for(int i = 0; i < rutaSecuencial.size() - 1; i++) {
            NodoEntity origen = rutaSecuencial.get(i);
            NodoEntity destino = rutaSecuencial.get(i + 1);

            RutaEntity ruta = buscarRutaDirecta(origen, destino);

            if(ruta != null) {
                pesoTotal += pesoService.calcularPeso(ruta, destino.getUrgencia());
            } else {
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
}
