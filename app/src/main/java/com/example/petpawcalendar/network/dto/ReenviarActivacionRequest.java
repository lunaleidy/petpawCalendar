package com.example.petpawcalendar.network.dto;

public class ReenviarActivacionRequest {

    private String correo;

    public ReenviarActivacionRequest(String correo) {
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

}
