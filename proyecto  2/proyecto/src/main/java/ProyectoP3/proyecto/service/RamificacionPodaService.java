package ProyectoP3.proyecto.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ProyectoP3.proyecto.model.NodoEntity;
import ProyectoP3.proyecto.model.RutaEntity;

@Service
public class RamificacionPodaService {

    private final PesoService pesoService;

    @Autowired
    public RamificacionPodaService(PesoService pesoService) {
        this.pesoService = pesoService;
    }

    double mejorPeso = Double.MAX_VALUE;
    List<NodoEntity> mejorRuta = new ArrayList<>();

    public List<NodoEntity> buscarRutaOptima(NodoEntity inicio, NodoEntity destino) {
        mejorPeso = Double.MAX_VALUE;
        mejorRuta.clear();
        Set<String> visitados = new HashSet<>();
        List<NodoEntity> rutaActual = new ArrayList<>();
        explorar(inicio, destino, visitados, rutaActual, 0);
        return mejorRuta;
    }

    private void explorar(NodoEntity actual, NodoEntity destino, Set<String> visitados, List<NodoEntity> rutaActual, double peso) {
        if (actual == null) return;

        if (peso >= mejorPeso) return;

        if (actual.getNombre().equalsIgnoreCase(destino.getNombre())) {
            rutaActual.add(actual);
            mejorPeso = peso;
            mejorRuta = new ArrayList<>(rutaActual);
            rutaActual.remove(rutaActual.size() - 1);
            return;
        }

        if (visitados.contains(actual.getNombre())) return;

        visitados.add(actual.getNombre());
        rutaActual.add(actual);

        if (actual.getRutas() != null) {
            for (RutaEntity r : actual.getRutas()) {
                NodoEntity sig = r.getDestino();
                if (sig != null && !visitados.contains(sig.getNombre())) {
                  
                    double nuevoPeso = peso + pesoService.calcularPeso(r, sig.getUrgencia());
                    explorar(sig, destino, visitados, rutaActual, nuevoPeso);
                }
            }
        }

        rutaActual.remove(rutaActual.size() - 1);
        visitados.remove(actual.getNombre());
    }
}
