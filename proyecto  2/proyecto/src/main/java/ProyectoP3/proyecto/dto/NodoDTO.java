package ProyectoP3.proyecto.dto;


import java.util.ArrayList;
import java.util.List;
public class NodoDTO {
    public String nombre;
    public String tipo;
    public int urgencia;
    public int personasAfectadas;
    public double x; 
    public double y; 
    public List<RutaDTO> rutas = new ArrayList<>(); 
}

