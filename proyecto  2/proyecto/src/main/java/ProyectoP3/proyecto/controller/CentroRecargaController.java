package ProyectoP3.proyecto.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ProyectoP3.proyecto.model.CentroRecargaEntity;
import ProyectoP3.proyecto.repo.CentroRecargaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/recargas")
public class CentroRecargaController {

    private final CentroRecargaRepository recargaRepo;

    public CentroRecargaController(CentroRecargaRepository recargaRepo) {
        this.recargaRepo = recargaRepo;
    }

    // Crear o actualizar centro de recarga
    @PutMapping
    public Mono<CentroRecargaEntity> createOrUpdateRecarga(@RequestBody CentroRecargaEntity nuevaRecarga) {
        return recargaRepo.save(nuevaRecarga);
    }

    // Listar todos los centros de recarga
    @GetMapping(value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<CentroRecargaEntity> getRecargas() {
        return recargaRepo.findAll();
    }

    // Buscar centro de recarga por nombre
    @GetMapping("/{nombre}")
    public Mono<CentroRecargaEntity> getRecargaByNombre(@PathVariable String nombre) {
        return recargaRepo.findOneByNombre(nombre);
    }
}
