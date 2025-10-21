package ProyectoP3.proyecto.model;


public class RutaRequest {
    private String origen;
    private String destino;
    private int tiempo;
    private int energia;
    private String clima;
    private double obstaculos;

    // Getters y setters

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getEnergia() {
        return energia;
    }

    public void setEnergia(int energia) {
        this.energia = energia;
    }

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public double getObstaculos() {
        return obstaculos;
    }

    public void setObstaculos(double obstaculos) {
        this.obstaculos = obstaculos;
    }
}