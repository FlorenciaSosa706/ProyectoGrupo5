package ProyectoP3.proyecto.repo;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import ProyectoP3.proyecto.model.HospitalEntity;
import reactor.core.publisher.Mono;

public interface HospitalRepository extends ReactiveNeo4jRepository<HospitalEntity, String> {
    Mono<HospitalEntity> findOneByNombre(String nombre);
}
