package com.example.bancosangremobile.validation;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
public class DonanteValidator {
    public boolean esDniValido(String dni) {
        return validarDni(dni).esValido();
    }

    public ResultadoValidacion validarDni(String dni) {
        if (dni == null || dni.isEmpty()) {
            return new ResultadoValidacion(
                    false,
                    "El DNI es obligatorio"
            );
        }
        if (!dni.matches("[0-9]+")) {
            return new ResultadoValidacion(
                    false,
                    "El DNI debe contener únicamente números"
            );
        }
        if (dni.length() < 7 || dni.length() > 8) {
            return new ResultadoValidacion(
                    false,
                    "El DNI debe tener 7 u 8 dígitos"
            );
        }
        return new ResultadoValidacion(true, "");
    }

    public ResultadoValidacion validarNombre(String nombre) {
        return validarTextoPersonal(nombre, "nombre");
    }
    public ResultadoValidacion validarApellido(String apellido) {
        return validarTextoPersonal(apellido, "apellido");
    }

    private ResultadoValidacion validarTextoPersonal(
            String texto,
            String campo
    ) {
        if (texto == null || texto.trim().isEmpty()) {
            return new ResultadoValidacion(
                    false,
                    "El " + campo + " es obligatorio"
            );
        }
        if (texto.length() > 100) {
            return new ResultadoValidacion(
                    false,
                    "El " + campo + " no puede superar los 100 caracteres"
            );
        }
        if (!texto.matches("[\\p{L}]+(?:[ '\\-][\\p{L}]+)*")) {
            return new ResultadoValidacion(
                    false,
                    "El " + campo + " contiene caracteres no permitidos"
            );
        }
        return new ResultadoValidacion(true, "");
    }


    public ResultadoValidacion validarFechaNacimiento(String fecha) {

        if (fecha == null || fecha.trim().isEmpty()) {
            return new ResultadoValidacion(
                    false,
                    "La fecha de nacimiento es obligatoria"
            );
        }

        try {
            DateTimeFormatter formato = DateTimeFormatter
                    .ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

            LocalDate fechaNacimiento = LocalDate.parse(fecha, formato);

            if (fechaNacimiento.isAfter(LocalDate.now())) {
                return new ResultadoValidacion(
                        false,
                        "La fecha de nacimiento no puede ser futura"
                );
            }

            return new ResultadoValidacion(true, "");

        } catch (DateTimeParseException e) {
            return new ResultadoValidacion(
                    false,
                    "La fecha de nacimiento no es válida"
            );
        }
    }


    public ResultadoValidacion validarTelefono(String telefono) {

        if (telefono == null || telefono.trim().isEmpty()) {
            return new ResultadoValidacion(
                    false,
                    "El teléfono es obligatorio"
            );
        }

        if (!telefono.matches("[0-9]+")) {
            return new ResultadoValidacion(
                    false,
                    "El teléfono debe contener únicamente números"
            );
        }

        if (telefono.length() < 8 || telefono.length() > 15) {
            return new ResultadoValidacion(
                    false,
                    "El teléfono debe tener entre 8 y 15 dígitos"
            );
        }

        return new ResultadoValidacion(true, "");
    }


    public ResultadoValidacion validarSexo(String sexo) {

        if (sexo == null || sexo.trim().isEmpty()) {
            return new ResultadoValidacion(
                    false,
                    "El sexo es obligatorio"
            );
        }

        if (!sexo.equals("Masculino") && !sexo.equals("Femenino")) {
            return new ResultadoValidacion(
                    false,
                    "El sexo seleccionado no es válido"
            );
        }

        return new ResultadoValidacion(true, "");
    }


    public ResultadoValidacion validarEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            return new ResultadoValidacion(true, "");
        }

        if (email.length() > 254) {
            return new ResultadoValidacion(
                    false,
                    "El correo electrónico es demasiado largo"
            );
        }

        if (!email.matches(
                "[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(?:\\.[A-Za-z0-9-]+)+"
        )) {
            return new ResultadoValidacion(
                    false,
                    "El formato del correo electrónico no es válido"
            );
        }

        return new ResultadoValidacion(true, "");
    }


    public ResultadoValidacion validarProvincia(String provincia) {
        if (provincia == null || provincia.trim().isEmpty()) {
            return new ResultadoValidacion(false, "La provincia es obligatoria");
        }

        return new ResultadoValidacion(true, null);
    }

    public ResultadoValidacion validarPartido(String partido) {
        if (partido == null || partido.trim().isEmpty()) {
            return new ResultadoValidacion(false, "El partido es obligatorio");
        }

        return new ResultadoValidacion(true, null);
    }

    public ResultadoValidacion validarLocalidad(String localidad) {
        if (localidad == null || localidad.trim().isEmpty()) {
            return new ResultadoValidacion(false, "La localidad es obligatoria");
        }

        return new ResultadoValidacion(true, null);
    }

    public ResultadoValidacion validarCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.trim().isEmpty()) {
            return new ResultadoValidacion(false, "El código postal es obligatorio");
        }

        if (!codigoPostal.trim().matches("\\d{4}")) {
            return new ResultadoValidacion(false,
                    "El código postal debe tener 4 dígitos");
        }

        return new ResultadoValidacion(true, null);
    }

    public ResultadoValidacion validarGrupoSanguineo(String grupoSanguineo) {
        if (grupoSanguineo == null || grupoSanguineo.trim().isEmpty()) {
            return new ResultadoValidacion(true, null);
        }

        String grupo = grupoSanguineo.trim().toUpperCase(java.util.Locale.ROOT);

        if (!grupo.equals("A") &&
                !grupo.equals("B") &&
                !grupo.equals("AB") &&
                !grupo.equals("O")) {

            return new ResultadoValidacion(false,
                    "El grupo sanguíneo no es válido");
        }

        return new ResultadoValidacion(true, null);
    }

    public ResultadoValidacion validarFactorRh(String factorRh) {
        if (factorRh == null || factorRh.trim().isEmpty()) {
            return new ResultadoValidacion(true, null);
        }

        String factor = factorRh.trim();

        if (!factor.equals("+") && !factor.equals("-")) {
            return new ResultadoValidacion(false,
                    "El factor Rh no es válido");
        }

        return new ResultadoValidacion(true, null);
    }


}

