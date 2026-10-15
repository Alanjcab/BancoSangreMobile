
package com.example.bancosangremobile.model;

public class DatosDonanteFormulario {

    private final String dni;
    private final String nombre;
    private final String apellido;
    private final String fechaNacimiento;
    private final String telefono;
    private final String sexo;
    private final String email;

    private final String provincia;
    private final String partido;
    private final String localidad;
    private final String codigoPostal;


    private final String grupoSanguineo;
    private final String factorRh;

    public DatosDonanteFormulario(
            String dni,
            String nombre,
            String apellido,
            String fechaNacimiento,
            String telefono,
            String sexo,
            String email) {

        this(
                dni,
                nombre,
                apellido,
                fechaNacimiento,
                telefono,
                sexo,
                email,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public DatosDonanteFormulario(
            String dni,
            String nombre,
            String apellido,
            String fechaNacimiento,
            String telefono,
            String sexo,
            String email,
            String provincia,
            String partido,
            String localidad,
            String codigoPostal,
            String grupoSanguineo,
            String factorRh) {

        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.sexo = sexo;
        this.email = email;

        this.provincia = provincia;
        this.partido = partido;
        this.localidad = localidad;
        this.codigoPostal = codigoPostal;

        this.grupoSanguineo = grupoSanguineo;
        this.factorRh = factorRh;
    }


    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getSexo() {
        return sexo;
    }

    public String getEmail() {
        return email;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getPartido() {
        return partido;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }


    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public String getFactorRh() {
        return factorRh;
    }
}
