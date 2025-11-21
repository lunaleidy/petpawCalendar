package com.example.petpawcalendar.network.dto;

public class ActivarCuentaRequest {

    private String correo;
    private String codigo;

    public ActivarCuentaRequest(String correo, String codigo) {
        this.correo = correo;
        this.codigo = codigo;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCodigo() {
        return codigo;
    }

}
