package ProyectoP3.proyecto.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ProyectoP3.proyecto.model.HospitalEntity;
import ProyectoP3.proyecto.repo.HospitalRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hospitales")
public class HospitalController {

    private final HospitalRepository hospitalRepo;

    public HospitalController(HospitalRepository hospitalRepo) {
        this.hospitalRepo = hospitalRepo;
    }

    // Crear o actualizar hospital
    @PutMapping
    public Mono<HospitalEntity> createOrUpdateHospital(@RequestBody HospitalEntity nuevoHospital) {
        return hospitalRepo.save(nuevoHospital);
    }

    // Listar todos los hospitales
    @GetMapping(value = { "", "/" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<HospitalEntity> getHospitales() {
        return hospitalRepo.findAll();
    }

    // Buscar hospital por nombre
    @GetMapping("/{nombre}")
    public Mono<HospitalEntity> getHospitalByNombre(@PathVariable String nombre) {
        return hospitalRepo.findOneByNombre(nombre);
    }
}
