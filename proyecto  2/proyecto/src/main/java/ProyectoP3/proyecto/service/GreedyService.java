package ProyectoP3.proyecto.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class GreedyService {

    private final PesoService pesoService;

    //@Autowired
    public GreedyService(PesoService pesoService) {
        this.pesoService = pesoService;
    }

    public List<NodoEntity> encontrarRutaGreedy(NodoEntity inicio, NodoEntity destino) {
        Set<NodoEntity> visitados = new HashSet<>();
        List<NodoEntity> ruta = new ArrayList<>();
        NodoEntity actual = inicio;

        while (actual != null && !actual.equals(destino)) {
            ruta.add(actual);
            visitados.add(actual);

            RutaEntity mejorRuta = actual.getRutas().stream()
                .filter(r -> !visitados.contains(r.getDestino()))
                .min(Comparator.comparingDouble(r -> pesoService.calcularPeso(r, r.getDestino().getUrgencia())))
                .orElse(null);

            if (mejorRuta == null) break;
            actual = mejorRuta.getDestino();
        }

        ruta.add(destino);
        return ruta;
    }
}
