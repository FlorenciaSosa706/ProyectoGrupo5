package ProyectoP3.proyecto.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ProyectoP3.proyecto.model.ZonaAfectadaEntity;
import ProyectoP3.proyecto.repo.ZonaAfectadaRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/zonas")
public class ZonaAfectadaController {

    private final ZonaAfectadaRepository zonaRepo;

    public ZonaAfectadaController(ZonaAfectadaRepository zonaRepo) {
        this.zonaRepo = zonaRepo;
    }

    // Crear o actualizar zona afectada
    @PutMapping
    public Mono<ZonaAfectadaEntity> createOrUpdateZona(@RequestBody ZonaAfectadaEntity nuevaZona) {
        return zonaRepo.save(nuevaZona);
    }

    // Listar todas las zonas afectadas
    @GetMapping(value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ZonaAfectadaEntity> getZonas() {
        return zonaRepo.findAll();
    }

    // Buscar zona afectada por nombre
    @GetMapping("/{nombre}")
    public Mono<ZonaAfectadaEntity> getZonaByNombre(@PathVariable String nombre) {
        return zonaRepo.findOneByNombre(nombre);
    }
}
