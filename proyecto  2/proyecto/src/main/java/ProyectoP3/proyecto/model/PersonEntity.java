package ProyectoP3.proyecto.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Person")
public class PersonEntity {

    @Id
    private String name;

    private Integer born;

    // Constructor vacío para Jackson
    public PersonEntity() {}

    // Constructor principal
    public PersonEntity(String name, Integer born) {
        this.name = name;
        this.born = born;
    }

    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getBorn() { return born; }
    public void setBorn(Integer born) { this.born = born; }
}
