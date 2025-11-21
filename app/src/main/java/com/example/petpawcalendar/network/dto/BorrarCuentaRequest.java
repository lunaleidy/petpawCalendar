package com.example.petpawcalendar.network.dto;

public class BorrarCuentaRequest {

    private String clave;

    public BorrarCuentaRequest() {
    }

    public BorrarCuentaRequest(String clave) {
        this.clave = clave;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}
