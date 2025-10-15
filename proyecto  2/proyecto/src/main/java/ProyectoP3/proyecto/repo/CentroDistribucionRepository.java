package ProyectoP3.proyecto.repo;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import ProyectoP3.proyecto.model.CentroDistribucionEntity;
import reactor.core.publisher.Mono;

public interface CentroDistribucionRepository extends ReactiveNeo4jRepository<CentroDistribucionEntity, String> {
    Mono<CentroDistribucionEntity> findOneByNombre(String nombre);
}
