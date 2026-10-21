
package com.example.bancosangremobile.model;

public class Donante {

    private final int idDonante;
    private final String nombre;
    private final String dni;
    private final String grupoSanguineo;
    private final String estado;

    // Constructor anterior: compatibilidad con código existente.
    public Donante(String nombre, String dni,
                   String grupoSanguineo, String estado) {

        this(0, nombre, dni, grupoSanguineo, estado);
    }

    // Constructor para donantes provenientes de MySQL.
    public Donante(int idDonante, String nombre,
                   String dni, String grupoSanguineo,
                   String estado) {

        this.idDonante = idDonante;
        this.nombre = nombre;
        this.dni = dni;
        this.grupoSanguineo = grupoSanguineo;
        this.estado = estado;
    }

    public int getIdDonante() {
        return idDonante;
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
