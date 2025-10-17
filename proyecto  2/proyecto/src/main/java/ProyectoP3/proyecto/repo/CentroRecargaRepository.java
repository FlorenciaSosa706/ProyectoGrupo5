package ProyectoP3.proyecto.repo;

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import ProyectoP3.proyecto.model.CentroRecargaEntity;
import reactor.core.publisher.Mono;

public interface CentroRecargaRepository extends ReactiveNeo4jRepository<CentroRecargaEntity, String> {
    Mono<CentroRecargaEntity> findOneByNombre(String nombre);
}
