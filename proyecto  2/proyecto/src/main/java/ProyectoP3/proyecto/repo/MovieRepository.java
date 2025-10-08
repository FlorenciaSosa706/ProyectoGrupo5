package ProyectoP3.proyecto.repo;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;

import ProyectoP3.proyecto.model.MovieEntity;
import reactor.core.publisher.Mono;


 public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, 
String> {
    Mono<MovieEntity> findOneByTitle(String title);
 }

