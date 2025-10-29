// Archivo: ProyectoP3/proyecto/dto/NodoDTO.java
package ProyectoP3.proyecto.dto;


import java.util.ArrayList;
import java.util.List;

public class NodoDTO {
    public String nombre;
    public String tipo;
    public int urgencia;
    public int personasAfectadas;
    public double x; // Asegúrate de que tu NodoEntity los tenga y se mapeen
    public double y; // Asegúrate de que tu NodoEntity los tenga y se mapeen
    public List<RutaDTO> rutas = new ArrayList<>(); // Rutas salientes del nodo
}

