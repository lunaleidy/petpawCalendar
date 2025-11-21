package com.example.petpawcalendar.network.dto;

public class ActividadRequest {

    private Integer id;
    private MascotaRef mascota;
    private TipoRef tipo;
    private String titulo;
    private String descripcion;
    private String fechaInicio;      // "yyyy-MM-dd'T'HH:mm:ss" o null
    private Boolean todoDia;
    private String repeticion;       // "ninguna" por defecto
    private Integer repeticionCada;  // null si no se repite
    private String repeticionHasta;  // null
    private String fechaDia;         // "yyyy-MM-dd"

    public static class MascotaRef {
        private Integer id;

        public MascotaRef(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    public static class TipoRef {
        private Integer id;

        public TipoRef(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MascotaRef getMascota() {
        return mascota;
    }

    public void setMascota(MascotaRef mascota) {
        this.mascota = mascota;
    }

    public TipoRef getTipo() {
        return tipo;
    }

    public void setTipo(TipoRef tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Boolean getTodoDia() {
        return todoDia;
    }

    public void setTodoDia(Boolean todoDia) {
        this.todoDia = todoDia;
    }

    public String getRepeticion() {
        return repeticion;
    }

    public void setRepeticion(String repeticion) {
        this.repeticion = repeticion;
    }

    public Integer getRepeticionCada() {
        return repeticionCada;
    }

    public void setRepeticionCada(Integer repeticionCada) {
        this.repeticionCada = repeticionCada;
    }

    public String getRepeticionHasta() {
        return repeticionHasta;
    }

    public void setRepeticionHasta(String repeticionHasta) {
        this.repeticionHasta = repeticionHasta;
    }

    public String getFechaDia() {
        return fechaDia;
    }

    public void setFechaDia(String fechaDia) {
        this.fechaDia = fechaDia;
    }

}
