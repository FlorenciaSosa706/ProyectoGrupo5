package ProyectoP3.proyecto.model;

import static org.springframework.data.neo4j.core.schema.Relationship.Direction.*;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Movie")
public class MovieEntity {

    @Id
    private String title;

    @Property("tagline")
    private String description;

    @Relationship(type = "ACTED_IN", direction = INCOMING)
    private Set<PersonEntity> actors = new HashSet<>();

    @Relationship(type = "DIRECTED", direction = INCOMING)
    private Set<PersonEntity> directors = new HashSet<>();

    // Constructor vacío para que Jackson pueda deserializar
    public MovieEntity() {}

    // Constructor principal
    public MovieEntity(String title, String description) {
        this.title = title;
        this.description = description;
    }

    // Métodos para agregar relaciones
    public void addActor(PersonEntity actor) {
        this.actors.add(actor);
    }

    public void addDirector(PersonEntity director) {
        this.directors.add(director);
    }

    // Getters y setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<PersonEntity> getActors() { return actors; }
    public void setActors(Set<PersonEntity> actors) { this.actors = actors; }

    public Set<PersonEntity> getDirectors() { return directors; }
    public void setDirectors(Set<PersonEntity> directors) { this.directors = directors; }
}
