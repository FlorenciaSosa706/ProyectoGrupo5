package ProyectoP3.proyecto.repo;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import ProyectoP3.proyecto.model.ZonaAfectadaEntity;
import reactor.core.publisher.Mono;

public interface ZonaAfectadaRepository extends ReactiveNeo4jRepository<ZonaAfectadaEntity, String> {
    Mono<ZonaAfectadaEntity> findOneByNombre(String nombre);
}
