package com.example.petpawcalendar.network.dto;

import com.google.gson.annotations.SerializedName;

public class TipoActividadRequest {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String nombre;

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }

}
