package com.example.petpawcalendar.network.dto;

public class RegistroRequest {

    private String nombreCompleto;
    private String correo;
    private String clave;
    private Boolean aceptaTerminos;

    public RegistroRequest(String nombreCompleto, String correo, String clave, Boolean aceptaTerminos) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.clave = clave;
        this.aceptaTerminos = aceptaTerminos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getClave() {
        return clave;
    }

    public Boolean getAceptaTerminos() {
        return aceptaTerminos;
    }

}
