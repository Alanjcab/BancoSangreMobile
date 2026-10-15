
package com.example.bancosangremobile.model;

public class Ubicacion {

    private final String id;
    private final String nombre;

    public Ubicacion(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
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
