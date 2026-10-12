package com.example.bancosangremobile.model;

public class Donante {

    private final String nombre;
    private final String dni;
    private final String grupoSanguineo;
    private final String estado;

    public Donante(String nombre, String dni,
                   String grupoSanguineo, String estado) {
        this.nombre = nombre;
        this.dni = dni;
        this.grupoSanguineo = grupoSanguineo;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public String getEstado() {
        return estado;
    }
}
