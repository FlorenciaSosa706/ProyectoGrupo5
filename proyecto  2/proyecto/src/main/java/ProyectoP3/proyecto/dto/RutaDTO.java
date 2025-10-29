// Archivo: ProyectoP3/proyecto/dto/RutaDTO.java
package ProyectoP3.proyecto.dto;

public class RutaDTO {
    public String destino;
    public double tiempo;
    public double energia;
    public String clima;
    public double obstaculos;
    public double pesoTotal; // Para calcular en el frontend o si ya lo tienes en la entidad

    // Puedes añadir un constructor si lo prefieres
    public RutaDTO() {}

    // Constructor que toma una RutaEntity para mapear sus propiedades
    // Asumiendo que RutaEntity tiene getters para tiempo, energia, clima, obstaculos y getDestino().getUrgencia()
    public RutaDTO(ProyectoP3.proyecto.model.RutaEntity rutaEntity) {
        this.destino = rutaEntity.getDestino().getNombre();
        this.tiempo = rutaEntity.getTiempo();
        this.energia = rutaEntity.getEnergia();
        this.clima = rutaEntity.getClima();
        this.obstaculos = rutaEntity.getObstaculos();
        // Calcular pesoTotal aquí, o si ya lo tienes en RutaEntity, mapearlo
        // pesoTotal = (tiempo * 0.3) + (energia * 0.3) + (obstaculos * 0.2) + (urgencia * 0.2)
        // Necesitas la urgencia del nodo destino para este cálculo
        this.pesoTotal = (rutaEntity.getTiempo() * 0.3) +
                         (rutaEntity.getEnergia() * 0.3) +
                         (rutaEntity.getObstaculos() * 0.2) +
                         (rutaEntity.getDestino().getUrgencia() * 0.2); // Asumo que getDestino() devuelve un NodoEntity válido
    }
}