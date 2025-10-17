package ProyectoP3.proyecto.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Node("CentroRecarga")
public class CentroRecargaEntity {

    @Id
    private String nombre;
    private int capacidadCarga;

    @Relationship(type = "CONECTA_CON")
    @JsonIgnoreProperties("centroRecarga")
    private Set<RutaEntity> rutas = new HashSet<>();

    public CentroRecargaEntity() {}

    public CentroRecargaEntity(String nombre, int capacidadCarga) {
        this.nombre = nombre;
        this.capacidadCarga = capacidadCarga;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidadCarga() { return capacidadCarga; }
    public void setCapacidadCarga(int capacidadCarga) { this.capacidadCarga = capacidadCarga; }

    public Set<RutaEntity> getRutas() { return rutas; }
    public void setRutas(Set<RutaEntity> rutas) { this.rutas = rutas; }
}
