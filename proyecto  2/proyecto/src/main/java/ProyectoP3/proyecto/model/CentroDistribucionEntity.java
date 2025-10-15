package ProyectoP3.proyecto.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Node("CentroDistribucion")
public class CentroDistribucionEntity {

    @Id
    private String nombre;

    @Property("stockMedicamentos")
    private int stockMedicamentos;

    @Property("capacidad")
    private int capacidad;

    @Relationship(type = "CONECTA_CON")
    @JsonIgnoreProperties("centroDistribucion")
    private Set<NodoEntity> conexiones = new HashSet<>();

    public CentroDistribucionEntity() {}

    public CentroDistribucionEntity(String nombre, int stockMedicamentos, int capacidad) {
        this.nombre = nombre;
        this.stockMedicamentos = stockMedicamentos;
        this.capacidad = capacidad;
    }

    public void agregarConexion(NodoEntity nodo) {
        this.conexiones.add(nodo);
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStockMedicamentos() { return stockMedicamentos; }
    public void setStockMedicamentos(int stockMedicamentos) { this.stockMedicamentos = stockMedicamentos; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public Set<NodoEntity> getConexiones() { return conexiones; }
    public void setConexiones(Set<NodoEntity> conexiones) { this.conexiones = conexiones; }
}
