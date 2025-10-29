// Archivo: ProyectoP3/proyecto/dto/RutaDTO.java
package ProyectoP3.proyecto.dto;

public class RutaDTO {
    public String destino;
    public double tiempo;
    public double energia;
    public String clima;
    public double obstaculos;
    public double pesoTotal; 
    public RutaDTO() {}

    
    public RutaDTO(ProyectoP3.proyecto.model.RutaEntity rutaEntity) {
        this.destino = rutaEntity.getDestino().getNombre();
        this.tiempo = rutaEntity.getTiempo();
        this.energia = rutaEntity.getEnergia();
        this.clima = rutaEntity.getClima();
        this.obstaculos = rutaEntity.getObstaculos();
       
        this.pesoTotal = (rutaEntity.getTiempo() * 0.3) +
                         (rutaEntity.getEnergia() * 0.3) +
                         (rutaEntity.getObstaculos() * 0.2) +
                         (rutaEntity.getDestino().getUrgencia() * 0.2); 
    }
}