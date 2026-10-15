
package com.example.bancosangremobile.controller;

import com.example.bancosangremobile.model.DatosDonanteFormulario;
import com.example.bancosangremobile.validation.ResultadoValidacionFormulario;

import org.junit.Test;

import static org.junit.Assert.*;

public class DonanteControllerTest {

    private final DonanteController controller = new DonanteController();

    private DatosDonanteFormulario crearFormulario(
            String provincia,
            String partido,
            String localidad,
            String codigoPostal,
            String grupo,
            String rh) {

        return new DatosDonanteFormulario(
                "12345678",
                "Juan",
                "Pérez",
                "15/08/1995",
                "2234567890",
                "Masculino",
                "juan@gmail.com",
                provincia,
                partido,
                localidad,
                codigoPostal,
                grupo,
                rh
        );
    }

    @Test
    public void formularioCorrectoEsValido() {
        DatosDonanteFormulario datos = crearFormulario(
                "Buenos Aires",
                "General Pueyrredón",
                "Mar del Plata",
                "7600",
                "A",
                "+"
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertTrue(resultado.esValido());
        assertTrue(resultado.getErrores().isEmpty());
    }

    @Test
    public void formularioConVariosErroresDetectaTodos() {
        DatosDonanteFormulario datos = new DatosDonanteFormulario(
                "12345",
                "Juan123",
                "Pérez",
                "15/08/1995",
                "123",
                "Masculino",
                "",
                "Buenos Aires",
                "General Pueyrredón",
                "Mar del Plata",
                "7600",
                "",
                ""
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertFalse(resultado.esValido());
        assertEquals(3, resultado.getErrores().size());

        assertEquals(
                "El DNI debe tener 7 u 8 dígitos",
                resultado.getError("dni")
        );

        assertEquals(
                "El nombre contiene caracteres no permitidos",
                resultado.getError("nombre")
        );

        assertEquals(
                "El teléfono debe tener entre 8 y 15 dígitos",
                resultado.getError("telefono")
        );
    }

    @Test
    public void formularioConEmailVacioEsValido() {
        DatosDonanteFormulario datos = new DatosDonanteFormulario(
                "12345678",
                "Ana",
                "García",
                "10/05/1998",
                "2234567890",
                "Femenino",
                "",
                "Buenos Aires",
                "General Pueyrredón",
                "Mar del Plata",
                "7600",
                "",
                ""
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertTrue(resultado.esValido());
    }

    @Test
    public void ubicacionVaciaEsInvalida() {
        DatosDonanteFormulario datos = crearFormulario(
                "", "", "", "", "", ""
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertFalse(resultado.esValido());
        assertEquals(4, resultado.getErrores().size());

        assertNotNull(resultado.getError("provincia"));
        assertNotNull(resultado.getError("partido"));
        assertNotNull(resultado.getError("localidad"));
        assertNotNull(resultado.getError("codigoPostal"));
    }

    @Test
    public void grupoSanguineoYRhVaciosSonValidos() {
        DatosDonanteFormulario datos = crearFormulario(
                "Buenos Aires",
                "General Pueyrredón",
                "Mar del Plata",
                "7600",
                "",
                ""
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertTrue(resultado.esValido());
    }

    @Test
    public void grupoSinRhEsInvalido() {
        DatosDonanteFormulario datos = crearFormulario(
                "Buenos Aires",
                "General Pueyrredón",
                "Mar del Plata",
                "7600",
                "A",
                ""
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertFalse(resultado.esValido());
        assertEquals(
                "Debés seleccionar el factor Rh",
                resultado.getError("factorRh")
        );
    }

    @Test
    public void rhSinGrupoEsInvalido() {
        DatosDonanteFormulario datos = crearFormulario(
                "Buenos Aires",
                "General Pueyrredón",
                "Mar del Plata",
                "7600",
                "",
                "+"
        );

        ResultadoValidacionFormulario resultado =
                controller.validarFormulario(datos);

        assertFalse(resultado.esValido());
        assertEquals(
                "Debés seleccionar el grupo sanguíneo",
                resultado.getError("grupoSanguineo")
        );
    }
}
