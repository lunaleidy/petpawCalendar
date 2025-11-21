package com.example.petpawcalendar.network.dto;

import com.google.gson.annotations.SerializedName;

public class UsuarioPerfilRequest {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nombreCompleto")
    private String nombreCompleto;

    @SerializedName("correo")
    private String correo;

    @SerializedName("activo")
    private Boolean activo;

    @SerializedName("fotoUrl")
    private String fotoUrl;

    @SerializedName("ultimoLogin")
    private String ultimoLogin; // lo recibes como String ISO

    @SerializedName("aceptoTerminos")
    private Boolean aceptoTerminos;

    public UsuarioPerfilRequest() {
    }

    public Integer getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public Boolean getActivo() {
        return activo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getUltimoLogin() {
        return ultimoLogin;
    }

    public Boolean getAceptoTerminos() {
        return aceptoTerminos;
    }

}
