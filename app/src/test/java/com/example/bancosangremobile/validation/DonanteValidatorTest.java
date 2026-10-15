package com.example.bancosangremobile.validation;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DonanteValidatorTest {

    private final DonanteValidator validator = new DonanteValidator();

    @Test
    public void dniConOchoDigitosEsValido() {
        assertTrue(validator.esDniValido("12345678"));
    }

    @Test
    public void dniConSieteDigitosEsValido() {
        assertTrue(validator.esDniValido("1234567"));
    }

    @Test
    public void dniConLetrasEsInvalido() {
        assertFalse(validator.esDniValido("12ABC678"));
    }

    @Test
    public void dniVacioEsInvalido() {
        assertFalse(validator.esDniValido(""));
    }

    @Test
    public void dniNuloEsInvalido() {
        assertFalse(validator.esDniValido(null));
    }

    @Test
    public void dniDemasiadoLargoEsInvalido() {
        assertFalse(validator.esDniValido("123456789"));
    }

    @Test
    public void dniConEspaciosEsInvalido() {
        assertFalse(validator.esDniValido(" 12345678 "));
    }



    @Test
    public void dniVacioDevuelveMensajeObligatorio() {

        ResultadoValidacion resultado = validator.validarDni("");

        assertFalse(resultado.esValido());
        assertEquals("El DNI es obligatorio", resultado.getMensaje());
    }

    @Test
    public void dniConLetrasDevuelveMensajeNumerico() {

        ResultadoValidacion resultado = validator.validarDni("12ABC678");

        assertFalse(resultado.esValido());
        assertEquals(
                "El DNI debe contener únicamente números",
                resultado.getMensaje()
        );
    }

    @Test
    public void dniCortoDevuelveMensajeLongitud() {

        ResultadoValidacion resultado = validator.validarDni("12345");

        assertFalse(resultado.esValido());
        assertEquals(
                "El DNI debe tener 7 u 8 dígitos",
                resultado.getMensaje()
        );
    }

    @Test
    public void dniCorrectoDevuelveResultadoValido() {

        ResultadoValidacion resultado = validator.validarDni("12345678");

        assertTrue(resultado.esValido());
        assertEquals("", resultado.getMensaje());
    }



    @Test
    public void nombreSimpleEsValido() {
        ResultadoValidacion resultado = validator.validarNombre("Juan");

        assertTrue(resultado.esValido());
    }

    @Test
    public void nombreCompuestoEsValido() {
        ResultadoValidacion resultado = validator.validarNombre("María José");

        assertTrue(resultado.esValido());
    }

    @Test
    public void nombreConApostrofeEsValido() {
        ResultadoValidacion resultado = validator.validarNombre("O'Connor");

        assertTrue(resultado.esValido());
    }

    @Test
    public void nombreConGuionEsValido() {
        ResultadoValidacion resultado = validator.validarNombre("Ana-María");

        assertTrue(resultado.esValido());
    }

    @Test
    public void nombreVacioEsInvalido() {
        ResultadoValidacion resultado = validator.validarNombre("");

        assertFalse(resultado.esValido());
        assertEquals("El nombre es obligatorio", resultado.getMensaje());
    }

    @Test
    public void nombreConEspaciosEsInvalido() {
        ResultadoValidacion resultado = validator.validarNombre("   ");

        assertFalse(resultado.esValido());
        assertEquals("El nombre es obligatorio", resultado.getMensaje());
    }

    @Test
    public void nombreNuloEsInvalido() {
        ResultadoValidacion resultado = validator.validarNombre(null);

        assertFalse(resultado.esValido());
        assertEquals("El nombre es obligatorio", resultado.getMensaje());
    }

    @Test
    public void nombreConNumerosEsInvalido() {
        ResultadoValidacion resultado = validator.validarNombre("Juan123");

        assertFalse(resultado.esValido());
        assertEquals(
                "El nombre contiene caracteres no permitidos",
                resultado.getMensaje()
        );
    }

    @Test
    public void nombreDemasiadoLargoEsInvalido() {
        String nombre = "A".repeat(101);

        ResultadoValidacion resultado = validator.validarNombre(nombre);

        assertFalse(resultado.esValido());
        assertEquals(
                "El nombre no puede superar los 100 caracteres",
                resultado.getMensaje()
        );
    }


    @Test
    public void apellidoSimpleEsValido() {
        ResultadoValidacion resultado = validator.validarApellido("González");

        assertTrue(resultado.esValido());
    }

    @Test
    public void apellidoCompuestoEsValido() {
        ResultadoValidacion resultado = validator.validarApellido("De la Cruz");

        assertTrue(resultado.esValido());
    }

    @Test
    public void apellidoConGuionEsValido() {
        ResultadoValidacion resultado = validator.validarApellido("Pérez-García");

        assertTrue(resultado.esValido());
    }

    @Test
    public void apellidoVacioEsInvalido() {
        ResultadoValidacion resultado = validator.validarApellido("");

        assertFalse(resultado.esValido());
        assertEquals("El apellido es obligatorio", resultado.getMensaje());
    }

    @Test
    public void apellidoNuloEsInvalido() {
        ResultadoValidacion resultado = validator.validarApellido(null);

        assertFalse(resultado.esValido());
        assertEquals("El apellido es obligatorio", resultado.getMensaje());
    }

    @Test
    public void apellidoConNumerosEsInvalido() {
        ResultadoValidacion resultado = validator.validarApellido("Pérez123");

        assertFalse(resultado.esValido());
        assertEquals(
                "El apellido contiene caracteres no permitidos",
                resultado.getMensaje()
        );
    }

    @Test
    public void apellidoDemasiadoLargoEsInvalido() {
        String apellido = "A".repeat(101);

        ResultadoValidacion resultado = validator.validarApellido(apellido);

        assertFalse(resultado.esValido());
        assertEquals(
                "El apellido no puede superar los 100 caracteres",
                resultado.getMensaje()
        );
    }


    @Test
    public void fechaNacimientoValida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("15/08/1995");

        assertTrue(resultado.esValido());
    }

    @Test
    public void fechaNacimientoVaciaEsInvalida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("");

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento es obligatoria",
                resultado.getMensaje()
        );
    }

    @Test
    public void fechaNacimientoImposibleEsInvalida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("31/02/2000");

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento no es válida",
                resultado.getMensaje()
        );
    }

    @Test
    public void fechaNacimientoBisiestaEsValida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("29/02/2000");

        assertTrue(resultado.esValido());
    }


    @Test
    public void fechaNacimientoFuturaEsInvalida() {
        String fechaFutura = LocalDate.now()
                .plusDays(1)
                .format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));

        ResultadoValidacion resultado =
                validator.validarFechaNacimiento(fechaFutura);

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento no puede ser futura",
                resultado.getMensaje()
        );
    }

    @Test
    public void fechaNacimientoNulaEsInvalida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento(null);

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento es obligatoria",
                resultado.getMensaje()
        );
    }

    @Test
    public void fechaNacimientoConEspaciosEsInvalida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("   ");

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento es obligatoria",
                resultado.getMensaje()
        );
    }

    @Test
    public void fechaNacimientoConMesInexistenteEsInvalida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("15/13/2000");

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento no es válida",
                resultado.getMensaje()
        );
    }

    @Test
    public void fechaNacimientoBisiestaIncorrectaEsInvalida() {
        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("29/02/2001");

        assertFalse(resultado.esValido());
        assertEquals(
                "La fecha de nacimiento no es válida",
                resultado.getMensaje()
        );
    }


    @Test
    public void fechaNacimientoAntiguaEsValida() {

        ResultadoValidacion resultado =
                validator.validarFechaNacimiento("15/08/1920");

        assertTrue(resultado.esValido());
    }


    @Test
    public void telefonoFijoEsValido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("2234567890");

        assertTrue(resultado.esValido());
    }

    @Test
    public void telefonoConCodigoPaisEsValido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("5491123456789");

        assertTrue(resultado.esValido());
    }

    @Test
    public void telefonoVacioEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("");

        assertFalse(resultado.esValido());
        assertEquals(
                "El teléfono es obligatorio",
                resultado.getMensaje()
        );
    }

    @Test
    public void telefonoNuloEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarTelefono(null);

        assertFalse(resultado.esValido());
        assertEquals(
                "El teléfono es obligatorio",
                resultado.getMensaje()
        );
    }

    @Test
    public void telefonoConLetrasEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("223ABC7890");

        assertFalse(resultado.esValido());
        assertEquals(
                "El teléfono debe contener únicamente números",
                resultado.getMensaje()
        );
    }

    @Test
    public void telefonoDemasiadoCortoEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("12345");

        assertFalse(resultado.esValido());
        assertEquals(
                "El teléfono debe tener entre 8 y 15 dígitos",
                resultado.getMensaje()
        );
    }

    @Test
    public void telefonoDemasiadoLargoEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("1234567890123456");

        assertFalse(resultado.esValido());
        assertEquals(
                "El teléfono debe tener entre 8 y 15 dígitos",
                resultado.getMensaje()
        );
    }

    @Test
    public void telefonoConEspaciosEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarTelefono("223 4567890");

        assertFalse(resultado.esValido());
        assertEquals(
                "El teléfono debe contener únicamente números",
                resultado.getMensaje()
        );
    }


    @Test
    public void sexoMasculinoEsValido() {
        ResultadoValidacion resultado =
                validator.validarSexo("Masculino");

        assertTrue(resultado.esValido());
    }

    @Test
    public void sexoFemeninoEsValido() {
        ResultadoValidacion resultado =
                validator.validarSexo("Femenino");

        assertTrue(resultado.esValido());
    }

    @Test
    public void sexoVacioEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarSexo("");

        assertFalse(resultado.esValido());
        assertEquals(
                "El sexo es obligatorio",
                resultado.getMensaje()
        );
    }

    @Test
    public void sexoNuloEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarSexo(null);

        assertFalse(resultado.esValido());
        assertEquals(
                "El sexo es obligatorio",
                resultado.getMensaje()
        );
    }

    @Test
    public void sexoNoReconocidoEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarSexo("OtroValor");

        assertFalse(resultado.esValido());
        assertEquals(
                "El sexo seleccionado no es válido",
                resultado.getMensaje()
        );
    }


    @Test
    public void emailValido() {
        ResultadoValidacion resultado =
                validator.validarEmail("juan@gmail.com");

        assertTrue(resultado.esValido());
    }

    @Test
    public void emailConPuntosEsValido() {
        ResultadoValidacion resultado =
                validator.validarEmail("maria.perez@outlook.com");

        assertTrue(resultado.esValido());
    }

    @Test
    public void emailVacioEsValido() {
        ResultadoValidacion resultado =
                validator.validarEmail("");

        assertTrue(resultado.esValido());
    }

    @Test
    public void emailNuloEsValido() {
        ResultadoValidacion resultado =
                validator.validarEmail(null);

        assertTrue(resultado.esValido());
    }

    @Test
    public void emailSinArrobaEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarEmail("juangmail.com");

        assertFalse(resultado.esValido());
        assertEquals(
                "El formato del correo electrónico no es válido",
                resultado.getMensaje()
        );
    }

    @Test
    public void emailSinExtensionEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarEmail("juan@gmail");

        assertFalse(resultado.esValido());
        assertEquals(
                "El formato del correo electrónico no es válido",
                resultado.getMensaje()
        );
    }

    @Test
    public void emailConEspaciosEsInvalido() {
        ResultadoValidacion resultado =
                validator.validarEmail("juan perez@gmail.com");

        assertFalse(resultado.esValido());
        assertEquals(
                "El formato del correo electrónico no es válido",
                resultado.getMensaje()
        );
    }

    @Test
    public void emailDemasiadoLargoEsInvalido() {
        String email = "a".repeat(250) + "@gmail.com";

        ResultadoValidacion resultado =
                validator.validarEmail(email);

        assertFalse(resultado.esValido());
        assertEquals(
                "El correo electrónico es demasiado largo",
                resultado.getMensaje()
        );
    }


    // ===== VALIDACIONES DE UBICACIÓN =====

    @Test
    public void provinciaValida() {
        assertTrue(validator.validarProvincia("Buenos Aires").esValido());
    }

    @Test
    public void provinciaVaciaEsInvalida() {
        ResultadoValidacion resultado = validator.validarProvincia("");
        assertFalse(resultado.esValido());
        assertEquals("La provincia es obligatoria", resultado.getMensaje());
    }

    @Test
    public void provinciaNulaEsInvalida() {
        assertFalse(validator.validarProvincia(null).esValido());
    }

    @Test
    public void partidoValido() {
        assertTrue(validator.validarPartido("General Pueyrredón").esValido());
    }

    @Test
    public void partidoVacioEsInvalido() {
        ResultadoValidacion resultado = validator.validarPartido("");
        assertFalse(resultado.esValido());
        assertEquals("El partido es obligatorio", resultado.getMensaje());
    }

    @Test
    public void partidoNuloEsInvalido() {
        assertFalse(validator.validarPartido(null).esValido());
    }

    @Test
    public void localidadValida() {
        assertTrue(validator.validarLocalidad("Mar del Plata").esValido());
    }

    @Test
    public void localidadVaciaEsInvalida() {
        ResultadoValidacion resultado = validator.validarLocalidad("");
        assertFalse(resultado.esValido());
        assertEquals("La localidad es obligatoria", resultado.getMensaje());
    }

    @Test
    public void localidadNulaEsInvalida() {
        assertFalse(validator.validarLocalidad(null).esValido());
    }

    // ===== CÓDIGO POSTAL =====

    @Test
    public void codigoPostalValido() {
        assertTrue(validator.validarCodigoPostal("7600").esValido());
    }

    @Test
    public void codigoPostalVacioEsInvalido() {
        ResultadoValidacion resultado = validator.validarCodigoPostal("");
        assertFalse(resultado.esValido());
        assertEquals("El código postal es obligatorio", resultado.getMensaje());
    }

    @Test
    public void codigoPostalConLetrasEsInvalido() {
        assertFalse(validator.validarCodigoPostal("76AB").esValido());
    }

    @Test
    public void codigoPostalCortoEsInvalido() {
        assertFalse(validator.validarCodigoPostal("760").esValido());
    }

    // ===== GRUPO SANGUÍNEO Y FACTOR RH =====

    @Test
    public void grupoSanguineoValido() {
        assertTrue(validator.validarGrupoSanguineo("AB").esValido());
    }

    @Test
    public void grupoSanguineoVacioEsValido() {
        assertTrue(validator.validarGrupoSanguineo("").esValido());
    }

    @Test
    public void grupoSanguineoIncorrectoEsInvalido() {
        assertFalse(validator.validarGrupoSanguineo("C").esValido());
    }

    @Test
    public void factorRhValido() {
        assertTrue(validator.validarFactorRh("+").esValido());
        assertTrue(validator.validarFactorRh("-").esValido());
    }

    @Test
    public void factorRhVacioEsValido() {
        assertTrue(validator.validarFactorRh("").esValido());
    }

    @Test
    public void factorRhIncorrectoEsInvalido() {
        assertFalse(validator.validarFactorRh("Positivo").esValido());
    }

}
