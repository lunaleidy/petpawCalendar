package com.example.petpawcalendar.network.dto;

public class ResetSolicitarRequest {

    private String correo;

    public ResetSolicitarRequest(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

}
