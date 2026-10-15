
package com.example.bancosangremobile.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.controller.DonanteController;
import com.example.bancosangremobile.controller.UbicacionController;
import com.example.bancosangremobile.model.DatosDonanteFormulario;
import com.example.bancosangremobile.model.Ubicacion;
import com.example.bancosangremobile.validation.ResultadoValidacionFormulario;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class NuevoDonanteActivity extends AppCompatActivity {

    // Componentes de fecha
    private LinearLayout btnFechaNacimiento;
    private TextView txtFechaNacimiento;

    // Controladores
    private DonanteController donanteController;
    private UbicacionController ubicacionController;

    // Datos personales
    private EditText edtDni;
    private EditText edtNombre;
    private EditText edtApellido;
    private EditText edtTelefono;
    private EditText edtEmail;
    private RadioGroup grupoSexo;

    // Ubicación
    private Spinner spinnerProvincia;
    private Spinner spinnerPartido;
    private Spinner spinnerLocalidad;
    private EditText edtCodigoPostal;

    // Información sanguínea
    private Spinner spinnerGrupoSanguineo;
    private Spinner spinnerFactorRh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags("es-AR")
        );

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nuevo_donante);

        // Inicializar controladores
        donanteController = new DonanteController();
        ubicacionController = new UbicacionController();

        // Vincular datos personales
        edtDni = findViewById(R.id.edtDni);
        edtNombre = findViewById(R.id.edtNombre);
        edtApellido = findViewById(R.id.edtApellido);
        edtTelefono = findViewById(R.id.edtTelefono);
        edtEmail = findViewById(R.id.edtEmail);
        grupoSexo = findViewById(R.id.grupoSexo);

        // Vincular ubicación
        spinnerProvincia = findViewById(R.id.spinnerProvincia);
        spinnerPartido = findViewById(R.id.spinnerPartido);
        spinnerLocalidad = findViewById(R.id.spinnerLocalidad);
        edtCodigoPostal = findViewById(R.id.edtCodigoPostal);

        // Vincular información sanguínea
        spinnerGrupoSanguineo = findViewById(R.id.spinnerGrupoSanguineo);
        spinnerFactorRh = findViewById(R.id.spinnerFactorRh);

        // Preparar campos dependientes
        configurarSpinnerVacio(
                spinnerPartido,
                "Primero seleccioná una provincia"
        );

        configurarSpinnerVacio(
                spinnerLocalidad,
                "Primero seleccioná un partido"
        );

        // Configurar eventos de selección
        configurarSeleccionProvincia();
        configurarSeleccionPartido();

        // Cargar provincias desde Georef
        cargarProvincias();

        // Botón registrar
        findViewById(R.id.btnRegistrarDonante)
                .setOnClickListener(v -> validarFormulario());

        // Calendario
        btnFechaNacimiento = findViewById(R.id.btnFechaNacimiento);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);

        btnFechaNacimiento.setOnClickListener(
                v -> mostrarCalendario()
        );

        // Botón volver
        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        // Ajustar pantalla
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void configurarSeleccionProvincia() {

        spinnerProvincia.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        configurarSpinnerVacio(
                                spinnerPartido,
                                "Primero seleccioná una provincia"
                        );

                        configurarSpinnerVacio(
                                spinnerLocalidad,
                                "Primero seleccioná un partido"
                        );

                        if (position == 0) {
                            return;
                        }

                        Object item = parent.getItemAtPosition(position);

                        if (!(item instanceof Ubicacion)) {
                            return;
                        }

                        Ubicacion provinciaSeleccionada =
                                (Ubicacion) item;

                        cargarDepartamentos(
                                provinciaSeleccionada.getId()
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                        configurarSpinnerVacio(
                                spinnerPartido,
                                "Primero seleccioná una provincia"
                        );

                        configurarSpinnerVacio(
                                spinnerLocalidad,
                                "Primero seleccioná un partido"
                        );
                    }
                }
        );
    }

    private void configurarSeleccionPartido() {

        spinnerPartido.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        configurarSpinnerVacio(
                                spinnerLocalidad,
                                "Primero seleccioná un partido"
                        );

                        if (position == 0) {
                            return;
                        }

                        Object item = parent.getItemAtPosition(position);

                        if (!(item instanceof Ubicacion)) {
                            return;
                        }

                        Ubicacion partidoSeleccionado =
                                (Ubicacion) item;

                        cargarLocalidades(
                                partidoSeleccionado.getId()
                        );
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {

                        configurarSpinnerVacio(
                                spinnerLocalidad,
                                "Primero seleccioná un partido"
                        );
                    }
                }
        );
    }

    private void cargarProvincias() {

        configurarSpinnerVacio(
                spinnerProvincia,
                "Cargando provincias..."
        );

        ubicacionController.obtenerProvincias(
                new UbicacionController.UbicacionCallback() {

                    @Override
                    public void onSuccess(JSONArray datos) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            ArrayList<Ubicacion> provincias =
                                    new ArrayList<>();

                            provincias.add(
                                    new Ubicacion(
                                            "",
                                            "Seleccionar provincia"
                                    )
                            );

                            try {

                                for (int i = 0; i < datos.length(); i++) {

                                    JSONObject provincia =
                                            datos.getJSONObject(i);

                                    String id =
                                            provincia.getString("id");

                                    String nombre =
                                            provincia.getString("nombre");

                                    provincias.add(
                                            new Ubicacion(id, nombre)
                                    );
                                }

                                mostrarUbicaciones(
                                        spinnerProvincia,
                                        provincias
                                );

                            } catch (Exception e) {

                                configurarSpinnerVacio(
                                        spinnerProvincia,
                                        "Error al cargar provincias"
                                );

                                mostrarMensaje(
                                        "Error al procesar las provincias"
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(String mensaje) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            configurarSpinnerVacio(
                                    spinnerProvincia,
                                    "No se pudieron cargar las provincias"
                            );

                            mostrarMensaje(mensaje);
                        });
                    }
                }
        );
    }

    private void cargarDepartamentos(String provinciaId) {

        configurarSpinnerVacio(
                spinnerPartido,
                "Cargando partidos..."
        );

        ubicacionController.obtenerDepartamentos(
                provinciaId,
                new UbicacionController.UbicacionCallback() {

                    @Override
                    public void onSuccess(JSONArray datos) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            if (!seleccionCoincide(
                                    spinnerProvincia,
                                    provinciaId)) {
                                return;
                            }

                            ArrayList<Ubicacion> departamentos =
                                    new ArrayList<>();

                            departamentos.add(
                                    new Ubicacion(
                                            "",
                                            "Seleccionar partido / departamento"
                                    )
                            );

                            try {

                                for (int i = 0; i < datos.length(); i++) {

                                    JSONObject departamento =
                                            datos.getJSONObject(i);

                                    String id =
                                            departamento.getString("id");

                                    String nombre =
                                            departamento.getString("nombre");

                                    departamentos.add(
                                            new Ubicacion(id, nombre)
                                    );
                                }

                                if (departamentos.size() <= 1) {

                                    configurarSpinnerVacio(
                                            spinnerPartido,
                                            "No se encontraron departamentos"
                                    );

                                    return;
                                }

                                mostrarUbicaciones(
                                        spinnerPartido,
                                        departamentos
                                );

                            } catch (Exception e) {

                                configurarSpinnerVacio(
                                        spinnerPartido,
                                        "Error al cargar partidos"
                                );

                                mostrarMensaje(
                                        "Error al procesar los partidos"
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(String mensaje) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            if (!seleccionCoincide(
                                    spinnerProvincia,
                                    provinciaId)) {
                                return;
                            }

                            configurarSpinnerVacio(
                                    spinnerPartido,
                                    "No se pudieron cargar los partidos"
                            );

                            mostrarMensaje(mensaje);
                        });
                    }
                }
        );
    }

    private void cargarLocalidades(String departamentoId) {

        configurarSpinnerVacio(
                spinnerLocalidad,
                "Cargando localidades..."
        );

        // Guardar también la provincia para comprobar
        // que no haya cambiado durante la consulta
        Object provinciaSeleccionada =
                spinnerProvincia.getSelectedItem();

        if (!(provinciaSeleccionada instanceof Ubicacion)) {
            return;
        }

        String provinciaId =
                ((Ubicacion) provinciaSeleccionada).getId();

        ubicacionController.obtenerLocalidades(
                departamentoId,
                new UbicacionController.UbicacionCallback() {

                    @Override
                    public void onSuccess(JSONArray datos) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            if (!seleccionCoincide(
                                    spinnerProvincia,
                                    provinciaId)) {
                                return;
                            }

                            if (!seleccionCoincide(
                                    spinnerPartido,
                                    departamentoId)) {
                                return;
                            }

                            ArrayList<Ubicacion> localidades =
                                    new ArrayList<>();

                            localidades.add(
                                    new Ubicacion(
                                            "",
                                            "Seleccionar localidad"
                                    )
                            );

                            try {

                                for (int i = 0; i < datos.length(); i++) {

                                    JSONObject localidad =
                                            datos.getJSONObject(i);

                                    String id =
                                            localidad.getString("id");

                                    String nombre =
                                            localidad.getString("nombre");

                                    localidades.add(
                                            new Ubicacion(id, nombre)
                                    );
                                }

                                if (localidades.size() <= 1) {

                                    configurarSpinnerVacio(
                                            spinnerLocalidad,
                                            "No se encontraron localidades"
                                    );

                                    return;
                                }

                                mostrarUbicaciones(
                                        spinnerLocalidad,
                                        localidades
                                );

                            } catch (Exception e) {

                                configurarSpinnerVacio(
                                        spinnerLocalidad,
                                        "Error al cargar localidades"
                                );

                                mostrarMensaje(
                                        "Error al procesar las localidades"
                                );
                            }
                        });
                    }

                    @Override
                    public void onError(String mensaje) {

                        runOnUiThread(() -> {

                            if (isFinishing() || isDestroyed()) {
                                return;
                            }

                            if (!seleccionCoincide(
                                    spinnerProvincia,
                                    provinciaId)) {
                                return;
                            }

                            if (!seleccionCoincide(
                                    spinnerPartido,
                                    departamentoId)) {
                                return;
                            }

                            configurarSpinnerVacio(
                                    spinnerLocalidad,
                                    "No se pudieron cargar las localidades"
                            );

                            mostrarMensaje(mensaje);
                        });
                    }
                }
        );
    }

    private void mostrarUbicaciones(
            Spinner spinner,
            ArrayList<Ubicacion> ubicaciones) {

        ArrayAdapter<Ubicacion> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        ubicaciones
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
        spinner.setEnabled(ubicaciones.size() > 1);
    }

    private void configurarSpinnerVacio(
            Spinner spinner,
            String mensaje) {

        ArrayList<String> opciones = new ArrayList<>();
        opciones.add(mensaje);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        opciones
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(adapter);
        spinner.setEnabled(false);
    }

    private boolean seleccionCoincide(
            Spinner spinner,
            String idEsperado) {

        Object seleccion = spinner.getSelectedItem();

        if (!(seleccion instanceof Ubicacion)) {
            return false;
        }

        Ubicacion ubicacion = (Ubicacion) seleccion;

        return idEsperado.equals(ubicacion.getId());
    }

    private String obtenerSeleccion(Spinner spinner) {

        if (spinner.getSelectedItem() == null) {
            return "";
        }

        if (spinner.getSelectedItemPosition() == 0) {
            return "";
        }

        return spinner.getSelectedItem()
                .toString()
                .trim();
    }

    private void mostrarMensaje(String mensaje) {

        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }

    private void validarFormulario() {

        String dni = edtDni.getText().toString();
        String nombre = edtNombre.getText().toString();
        String apellido = edtApellido.getText().toString();

        String fechaNacimiento =
                txtFechaNacimiento.getText().toString();

        String telefono = edtTelefono.getText().toString();
        String email = edtEmail.getText().toString();

        // Ubicación
        String provincia = obtenerSeleccion(spinnerProvincia);
        String partido = obtenerSeleccion(spinnerPartido);
        String localidad = obtenerSeleccion(spinnerLocalidad);

        String codigoPostal =
                edtCodigoPostal.getText().toString();

        // Información sanguínea
        String grupoSanguineo =
                obtenerSeleccion(spinnerGrupoSanguineo);

        String factorRh =
                obtenerSeleccion(spinnerFactorRh);

        // Sexo
        String sexo = "";

        int sexoSeleccionado =
                grupoSexo.getCheckedRadioButtonId();

        if (sexoSeleccionado == R.id.radioMasculino) {
            sexo = "Masculino";
        } else if (sexoSeleccionado == R.id.radioFemenino) {
            sexo = "Femenino";
        }

        // Convertir factor Rh al formato del validador
        if (factorRh.equalsIgnoreCase("Positivo")) {
            factorRh = "+";
        } else if (factorRh.equalsIgnoreCase("Negativo")) {
            factorRh = "-";
        }

        // Crear objeto con los 13 campos
        DatosDonanteFormulario datos =
                new DatosDonanteFormulario(
                        dni,
                        nombre,
                        apellido,
                        fechaNacimiento,
                        telefono,
                        sexo,
                        email,
                        provincia,
                        partido,
                        localidad,
                        codigoPostal,
                        grupoSanguineo,
                        factorRh
                );

        // Validar mediante el controlador
        ResultadoValidacionFormulario resultado =
                donanteController.validarFormulario(datos);

        // Errores en campos de texto
        edtDni.setError(resultado.getError("dni"));
        edtNombre.setError(resultado.getError("nombre"));
        edtApellido.setError(resultado.getError("apellido"));
        edtTelefono.setError(resultado.getError("telefono"));
        edtEmail.setError(resultado.getError("email"));

        edtCodigoPostal.setError(
                resultado.getError("codigoPostal")
        );

        // Error de fecha de nacimiento
        String errorFecha =
                resultado.getError("fechaNacimiento");

        txtFechaNacimiento.setError(errorFecha);

        // Error de sexo
        String errorSexo =
                resultado.getError("sexo");

        RadioButton radioMasculino =
                findViewById(R.id.radioMasculino);

        radioMasculino.setError(errorSexo);

        // Comprobar resultado general
        if (!resultado.esValido()) {

            String mensaje = resultado.getErrores()
                    .values()
                    .iterator()
                    .next();

            Toast.makeText(
                    this,
                    mensaje,
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        Toast.makeText(
                this,
                "Formulario validado correctamente",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void mostrarCalendario() {

        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (view,
                         anioSeleccionado,
                         mesSeleccionado,
                         diaSeleccionado) -> {

                            String fecha = String.format(
                                    Locale.getDefault(),
                                    "%02d/%02d/%04d",
                                    diaSeleccionado,
                                    mesSeleccionado + 1,
                                    anioSeleccionado
                            );

                            txtFechaNacimiento.setText(fecha);
                        },
                        anio,
                        mes,
                        dia
                );

        datePickerDialog.getDatePicker()
                .setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    @Override
    protected void onDestroy() {

        if (ubicacionController != null) {
            ubicacionController.cerrar();
        }

        super.onDestroy();
    }
}
