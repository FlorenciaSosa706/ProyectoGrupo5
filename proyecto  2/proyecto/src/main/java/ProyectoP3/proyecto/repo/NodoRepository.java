package ProyectoP3.proyecto.repo;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.stereotype.Repository;

import ProyectoP3.proyecto.model.NodoEntity;
import reactor.core.publisher.Mono;

@Repository
public interface NodoRepository extends ReactiveNeo4jRepository<NodoEntity, String> {
    Mono<NodoEntity> findByNombre(String nombre);
}