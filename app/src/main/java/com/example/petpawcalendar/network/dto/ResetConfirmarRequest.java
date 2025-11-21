package com.example.petpawcalendar.network.dto;

public class ResetConfirmarRequest {

    private String correo;
    private String codigo;
    private String nuevaClave;

    public ResetConfirmarRequest(String correo, String codigo, String nuevaClave) {
        this.correo = correo;
        this.codigo = codigo;
        this.nuevaClave = nuevaClave;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNuevaClave() {
        return nuevaClave;
    }

    public void setNuevaClave(String nuevaClave) {
        this.nuevaClave = nuevaClave;
    }

}
