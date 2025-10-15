package ProyectoP3.proyecto.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ProyectoP3.proyecto.model.CentroDistribucionEntity;
import ProyectoP3.proyecto.repo.CentroDistribucionRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/centros")
public class CentroDistribucionController {

    private final CentroDistribucionRepository centroRepo;

    public CentroDistribucionController(CentroDistribucionRepository centroRepo) {
        this.centroRepo = centroRepo;
    }

    // Crear o actualizar un centro de distribución
    @PutMapping
    public Mono<CentroDistribucionEntity> createOrUpdateCentro(@RequestBody CentroDistribucionEntity nuevoCentro) {
        return centroRepo.save(nuevoCentro);
    }

    // Listar todos los centros de distribución
    @GetMapping(value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
public Flux<CentroDistribucionEntity> getCentros() {
    return centroRepo.findAll();
}


    // Buscar un centro de distribución por nombre
    @GetMapping("/{nombre}")
    public Mono<CentroDistribucionEntity> getCentroByNombre(@PathVariable String nombre) {
        return centroRepo.findOneByNombre(nombre);
    }
}
