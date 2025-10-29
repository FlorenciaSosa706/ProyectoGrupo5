package ProyectoP3.proyecto.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("Nodo")
public class NodoEntity {

    @Id
    private String nombre;

    @Property("tipo")
    private String tipo; // Hospital, ZonaAfectada, CentroRecarga

    @Property("urgencia")
    private int urgencia;

    @Property("personasAfectadas")
    private int personasAfectadas;

    public NodoEntity() {}

    public NodoEntity(String nombre, String tipo, int urgencia, int personasAfectadas) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.urgencia = urgencia;
        this.personasAfectadas = personasAfectadas;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getUrgencia() { return urgencia; }
    public void setUrgencia(int urgencia) { this.urgencia = urgencia; }

    public int getPersonasAfectadas() { return personasAfectadas; }
    public void setPersonasAfectadas(int personasAfectadas) { this.personasAfectadas = personasAfectadas; }

    @Relationship(type = "CONECTA_CON")
    private Set<RutaEntity> rutas = new HashSet<>();

    public Set<RutaEntity> getRutas() { return rutas; }
    public void setRutas(Set<RutaEntity> rutas) { this.rutas = rutas; }








}