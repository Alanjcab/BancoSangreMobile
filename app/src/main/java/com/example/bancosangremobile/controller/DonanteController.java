package com.example.bancosangremobile.controller;

import com.example.bancosangremobile.validation.DonanteValidator;
import com.example.bancosangremobile.validation.ResultadoValidacion;
import com.example.bancosangremobile.model.DatosDonanteFormulario;
import com.example.bancosangremobile.validation.ResultadoValidacionFormulario;
public class DonanteController {

    private final DonanteValidator validator;

    public DonanteController() {
        this.validator = new DonanteValidator();
    }

    public ResultadoValidacion validarDni(String dni) {
        return validator.validarDni(dni);
    }

    public ResultadoValidacion validarNombre(String nombre) {
        return validator.validarNombre(nombre);
    }

    public ResultadoValidacion validarApellido(String apellido) {
        return validator.validarApellido(apellido);
    }

    public ResultadoValidacion validarFechaNacimiento(String fecha) {
        return validator.validarFechaNacimiento(fecha);
    }

    public ResultadoValidacion validarTelefono(String telefono) {
        return validator.validarTelefono(telefono);
    }

    public ResultadoValidacion validarSexo(String sexo) {
        return validator.validarSexo(sexo);
    }

    public ResultadoValidacion validarEmail(String email) {
        return validator.validarEmail(email);
    }


    public ResultadoValidacion validarProvincia(String provincia) {
        return validator.validarProvincia(provincia);
    }

    public ResultadoValidacion validarPartido(String partido) {
        return validator.validarPartido(partido);
    }

    public ResultadoValidacion validarLocalidad(String localidad) {
        return validator.validarLocalidad(localidad);
    }

    public ResultadoValidacion validarCodigoPostal(String codigoPostal) {
        return validator.validarCodigoPostal(codigoPostal);
    }

    public ResultadoValidacion validarGrupoSanguineo(String grupoSanguineo) {
        return validator.validarGrupoSanguineo(grupoSanguineo);
    }

    public ResultadoValidacion validarFactorRh(String factorRh) {
        return validator.validarFactorRh(factorRh);
    }

    public ResultadoValidacionFormulario validarFormulario(
            DatosDonanteFormulario datos) {

        ResultadoValidacionFormulario resultado =
                new ResultadoValidacionFormulario();

        ResultadoValidacion dni = validarDni(datos.getDni());
        if (!dni.esValido()) {
            resultado.agregarError("dni", dni.getMensaje());
        }

        ResultadoValidacion nombre = validarNombre(datos.getNombre());
        if (!nombre.esValido()) {
            resultado.agregarError("nombre", nombre.getMensaje());
        }

        ResultadoValidacion apellido = validarApellido(datos.getApellido());
        if (!apellido.esValido()) {
            resultado.agregarError("apellido", apellido.getMensaje());
        }

        ResultadoValidacion fecha =
                validarFechaNacimiento(datos.getFechaNacimiento());
        if (!fecha.esValido()) {
            resultado.agregarError("fechaNacimiento", fecha.getMensaje());
        }

        ResultadoValidacion telefono = validarTelefono(datos.getTelefono());
        if (!telefono.esValido()) {
            resultado.agregarError("telefono", telefono.getMensaje());
        }

        ResultadoValidacion sexo = validarSexo(datos.getSexo());
        if (!sexo.esValido()) {
            resultado.agregarError("sexo", sexo.getMensaje());
        }

        ResultadoValidacion email = validarEmail(datos.getEmail());
        if (!email.esValido()) {
            resultado.agregarError("email", email.getMensaje());
        }


        ResultadoValidacion provincia =
                validarProvincia(datos.getProvincia());

        if (!provincia.esValido()) {
            resultado.agregarError("provincia", provincia.getMensaje());
        }

        ResultadoValidacion partido =
                validarPartido(datos.getPartido());

        if (!partido.esValido()) {
            resultado.agregarError("partido", partido.getMensaje());
        }

        ResultadoValidacion localidad =
                validarLocalidad(datos.getLocalidad());

        if (!localidad.esValido()) {
            resultado.agregarError("localidad", localidad.getMensaje());
        }

        ResultadoValidacion codigoPostal =
                validarCodigoPostal(datos.getCodigoPostal());

        if (!codigoPostal.esValido()) {
            resultado.agregarError("codigoPostal", codigoPostal.getMensaje());
        }

        ResultadoValidacion grupo =
                validarGrupoSanguineo(datos.getGrupoSanguineo());

        if (!grupo.esValido()) {
            resultado.agregarError("grupoSanguineo", grupo.getMensaje());
        }

        ResultadoValidacion rh =
                validarFactorRh(datos.getFactorRh());

        if (!rh.esValido()) {
            resultado.agregarError("factorRh", rh.getMensaje());
        }

        // si se completa uno también debe completarse el otro
        boolean tieneGrupo = datos.getGrupoSanguineo() != null
                && !datos.getGrupoSanguineo().trim().isEmpty();

        boolean tieneRh = datos.getFactorRh() != null
                && !datos.getFactorRh().trim().isEmpty();

        if (tieneGrupo && !tieneRh) {
            resultado.agregarError(
                    "factorRh",
                    "Debés seleccionar el factor Rh"
            );
        }

        if (!tieneGrupo && tieneRh) {
            resultado.agregarError(
                    "grupoSanguineo",
                    "Debés seleccionar el grupo sanguíneo"
            );
        }


        return resultado;
    }

}
