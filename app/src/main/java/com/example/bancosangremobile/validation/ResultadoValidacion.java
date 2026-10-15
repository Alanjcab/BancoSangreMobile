
package com.example.bancosangremobile.validation;

public class ResultadoValidacion {

    private final boolean valido;
    private final String mensaje;

    public ResultadoValidacion(boolean valido, String mensaje) {
        this.valido = valido;
        this.mensaje = mensaje;
    }

    public boolean esValido() {
        return valido;
    }

    public String getMensaje() {
        return mensaje;
    }
}
