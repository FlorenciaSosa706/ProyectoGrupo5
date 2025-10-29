package ProyectoP3.proyecto.model;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

@RelationshipProperties
public class RutaEntity {

    @Id
    @GeneratedValue
    private Long id;

    private double tiempo;
    private double energia;
    private String clima;
    private double obstaculos;

    // 👇 Ahora usamos NodoEntity, no Object
    @TargetNode
    private NodoEntity destino;

    public RutaEntity() {}

    public RutaEntity(int tiempo, int energia, String clima, double obstaculos, NodoEntity destino) {
        this.tiempo = tiempo;
        this.energia = energia;
        this.clima = clima;
        this.obstaculos = obstaculos;
        this.destino = destino;
    }

    public Long getId() { return id; }
    public double getTiempo() { return tiempo; }
    public double getEnergia() { return energia; }
    public String getClima() { return clima; }
    public double getObstaculos() { return obstaculos; }
    public NodoEntity getDestino() { return destino; }
    
    public void setTiempo(double tiempo) { this.tiempo = tiempo; }
    public void setEnergia(double energia) { this.energia = energia; }
    public void setClima(String clima) { this.clima = clima; }
    public void setObstaculos(double obstaculos) { this.obstaculos = obstaculos; }
    public void setDestino(NodoEntity destino) { this.destino = destino; }
}