package ProyectoP3.proyecto.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Node("ZonaAfectada")
public class ZonaAfectadaEntity {

    @Id
    private String nombre;
    private int urgencia;
    private int personasAfectadas;

    @Relationship(type = "CONECTA_CON")
    @JsonIgnoreProperties("zonaAfectada")
    private Set<RutaEntity> rutas = new HashSet<>();

    public ZonaAfectadaEntity() {}

    public ZonaAfectadaEntity(String nombre, int urgencia, int personasAfectadas) {
        this.nombre = nombre;
        this.urgencia = urgencia;
        this.personasAfectadas = personasAfectadas;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getUrgencia() { return urgencia; }
    public void setUrgencia(int urgencia) { this.urgencia = urgencia; }

    public int getPersonasAfectadas() { return personasAfectadas; }
    public void setPersonasAfectadas(int personasAfectadas) { this.personasAfectadas = personasAfectadas; }

    public Set<RutaEntity> getRutas() { return rutas; }
    public void setRutas(Set<RutaEntity> rutas) { this.rutas = rutas; }
}
