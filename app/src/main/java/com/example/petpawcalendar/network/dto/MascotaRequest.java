package com.example.petpawcalendar.network.dto;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class MascotaRequest {

    @SerializedName("id")
    private Integer id;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("especie")
    private String especie;

    @SerializedName("raza")
    private String raza;

    @SerializedName("sexo")
    private String sexo;

    // Recibida como "yyyy-MM-dd" desde el backend
    @SerializedName("fechaNacimiento")
    private String fechaNacimiento;

    @SerializedName("pesoKg")
    private BigDecimal pesoKg;

    @SerializedName("fotoUrl")
    private String fotoUrl;

    public MascotaRequest() {
        // Constructor vacío necesario para Gson
    }

    public MascotaRequest(Integer id,
                          String nombre,
                          String especie,
                          String raza,
                          String sexo,
                          String fechaNacimiento,
                          BigDecimal pesoKg,
                          String fotoUrl) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoKg = pesoKg;
        this.fotoUrl = fotoUrl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public BigDecimal getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(BigDecimal pesoKg) {
        this.pesoKg = pesoKg;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

}
