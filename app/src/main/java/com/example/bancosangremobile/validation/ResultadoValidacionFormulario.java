package com.example.bancosangremobile.validation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
public class ResultadoValidacionFormulario {

    private final Map<String, String> errores = new LinkedHashMap<>();

    public void agregarError(String campo, String mensaje) {
        errores.put(campo, mensaje);
    }

    public boolean esValido() {
        return errores.isEmpty();
    }

    public String getError(String campo) {
        return errores.get(campo);
    }

    public Map<String, String> getErrores() {
        return Collections.unmodifiableMap(errores);
    }
}
