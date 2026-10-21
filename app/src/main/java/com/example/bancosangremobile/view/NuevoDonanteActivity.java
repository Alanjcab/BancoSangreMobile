
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
import com.example.bancosangremobile.repository.DonanteRepository;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class NuevoDonanteActivity extends AppCompatActivity {

    // Componentes de fecha
    private LinearLayout btnFechaNacimiento;
    private TextView txtFechaNacimiento;

    // Controladores
    private DonanteController donanteController;
    private UbicacionController ubicacionController;

    private DonanteRepository donanteRepository;
    private boolean guardando = false;

    private boolean modoEdicion = false;
    private int idDonanteEditar = 0;
    private boolean datosEdicionCargados = false;

    private String provinciaPendiente = "";
    private String partidoPendiente = "";
    private String localidadPendiente = "";

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
        donanteRepository = new DonanteRepository();

        modoEdicion = getIntent().getBooleanExtra(
                "modo_edicion", false
        );

        idDonanteEditar = getIntent().getIntExtra(
                "id_donante", 0
        );

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
        findViewById(R.id.btnRegistrarDonante).setOnClickListener(v -> validarFormulario());

        if (modoEdicion) {
            if (idDonanteEditar <= 0) {
                mostrarMensaje("ID de donante inválido");
                finish();
                return;
            }
            View boton = findViewById(R.id.btnRegistrarDonante);
            boton.setEnabled(false);

            if (boton instanceof TextView) {
                ((TextView) boton).setText("Guardar cambios");
            }
            cargarDonanteParaEditar();
        }

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

                                seleccionarPendiente(
                                        spinnerProvincia,
                                        provinciaPendiente
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
                                seleccionarPendiente(
                                        spinnerPartido,
                                        partidoPendiente
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
                                seleccionarPendiente(
                                        spinnerLocalidad,
                                        localidadPendiente
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

        if (modoEdicion && !datosEdicionCargados) {
            mostrarMensaje(
                    "Esperá a que se carguen los datos"
            );
            return;
        }

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

        registrarDonante(
                dni,
                nombre,
                apellido,
                fechaNacimiento,
                sexo,
                telefono,
                email,
                provincia,
                partido,
                localidad,
                codigoPostal,
                grupoSanguineo,
                factorRh
        );
    }


    private void registrarDonante(
            String dni,
            String nombre,
            String apellido,
            String fechaNacimiento,
            String sexo,
            String telefono,
            String email,
            String provincia,
            String partido,
            String localidad,
            String codigoPostal,
            String grupoSanguineo,
            String factorRh
    ) {

        if (guardando) {
            return;
        }

        JSONObject json = new JSONObject();

        try {

            SimpleDateFormat formatoPantalla =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.ROOT
                    );

            formatoPantalla.setLenient(false);

            SimpleDateFormat formatoApi =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.ROOT
                    );

            String fechaApi = formatoApi.format(formatoPantalla.parse(fechaNacimiento.trim()));

            json.put("dni", dni.trim());
            json.put("nombre", nombre.trim());
            json.put("apellido", apellido.trim());
            json.put("fecha_nacimiento", fechaApi);
            json.put("sexo", sexo.trim());
            json.put("telefono", telefono.trim());

            json.put("email", valorOpcional(email));

            json.put("provincia", valorOpcional(provincia));
            json.put("partido", valorOpcional(partido));
            json.put("localidad", valorOpcional(localidad));

            if (!modoEdicion) {json.put("domicilio", JSONObject.NULL);}

            json.put("codigo_postal", valorOpcional(codigoPostal));

            json.put("grupo_sanguineo", valorOpcional(grupoSanguineo));

            json.put("factor_rh", valorOpcional(factorRh));

            if (!modoEdicion) {
                json.put("fenotipo", JSONObject.NULL);
            }

            if (modoEdicion) {
                json.put("id_donante", idDonanteEditar);
            }

        } catch (ParseException | org.json.JSONException e) {
            mostrarMensaje("Error al preparar los datos del donante");
            return;
        }

        guardando = true;

        findViewById(R.id.btnRegistrarDonante)
                .setEnabled(false);

        new Thread(() -> {

            try {

                int idDonante;

                if (modoEdicion) {
                    donanteRepository.actualizarDonante(json);
                    idDonante = idDonanteEditar;
                } else {
                    idDonante = donanteRepository.crearDonante(json);
                }

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Toast.makeText(
                            this, (modoEdicion
                                    ? "Donante actualizado correctamente. ID: "
                                    : "Donante registrado correctamente. ID: ")
                                    + idDonante,
                            Toast.LENGTH_LONG
                    ).show();

                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    guardando = false;

                    findViewById(R.id.btnRegistrarDonante)
                            .setEnabled(true);

                    mostrarMensaje(
                            (modoEdicion
                                    ? "No se pudo actualizar: "
                                    : "No se pudo registrar: ")
                                    + e.getMessage()
                    );
                });
            }

        }).start();
    }


    private void cargarDonanteParaEditar() {

        new Thread(() -> {

            try {

                JSONObject donante =
                        donanteRepository.obtenerDonantePorId(
                                idDonanteEditar
                        );

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    try {

                        edtDni.setText(donante.optString("dni", ""));
                        edtNombre.setText(donante.optString("nombre", ""));
                        edtApellido.setText(donante.optString("apellido", ""));
                        edtTelefono.setText(donante.optString("telefono", ""));

                        edtEmail.setText(
                                donante.isNull("email")
                                        ? ""
                                        : donante.optString("email", "")
                        );

                        edtCodigoPostal.setText(
                                donante.isNull("codigo_postal")
                                        ? ""
                                        : donante.optString("codigo_postal", "")
                        );

                        String fecha = donante.optString(
                                "fecha_nacimiento", ""
                        );

                        if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {

                            SimpleDateFormat entrada =
                                    new SimpleDateFormat(
                                            "yyyy-MM-dd", Locale.ROOT
                                    );

                            SimpleDateFormat salida =
                                    new SimpleDateFormat(
                                            "dd/MM/yyyy", Locale.ROOT
                                    );

                            entrada.setLenient(false);

                            txtFechaNacimiento.setText(
                                    salida.format(entrada.parse(fecha))
                            );
                        }

                        String sexo = donante.optString("sexo", "");

                        if (sexo.equalsIgnoreCase("masculino")) {
                            grupoSexo.check(R.id.radioMasculino);
                        } else if (sexo.equalsIgnoreCase("femenino")) {
                            grupoSexo.check(R.id.radioFemenino);
                        }

                        seleccionarPendiente(
                                spinnerGrupoSanguineo,
                                donante.optString("grupo_sanguineo", "")
                        );

                        String rh = donante.optString("factor_rh", "");

                        seleccionarPendiente(
                                spinnerFactorRh,
                                rh.equals("+") ? "Positivo"
                                        : rh.equals("-") ? "Negativo" : rh
                        );

                        provinciaPendiente = donante.isNull("provincia")
                                ? "" : donante.optString("provincia", "");

                        partidoPendiente = donante.isNull("partido")
                                ? "" : donante.optString("partido", "");

                        localidadPendiente = donante.isNull("localidad")
                                ? "" : donante.optString("localidad", "");

                        seleccionarPendiente(
                                spinnerProvincia,
                                provinciaPendiente
                        );

                        datosEdicionCargados = true;

                        findViewById(R.id.btnRegistrarDonante)
                                .setEnabled(true);

                    } catch (Exception e) {

                        mostrarMensaje(
                                "Error al mostrar los datos: "
                                        + e.getMessage()
                        );
                    }
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    if (!isFinishing() && !isDestroyed()) {

                        mostrarMensaje(
                                "No se pudo cargar el donante: "
                                        + e.getMessage()
                        );

                        finish();
                    }
                });
            }

        }).start();
    }


    private void seleccionarPendiente(
            Spinner spinner,
            String valor
    ) {

        if (valor == null || valor.trim().isEmpty()
                || spinner.getAdapter() == null) {
            return;
        }

        for (int i = 1; i < spinner.getAdapter().getCount(); i++) {

            Object item = spinner.getAdapter().getItem(i);

            if (item == null) {
                continue;
            }

            boolean coincide = item.toString().trim()
                    .equalsIgnoreCase(valor.trim());

            if (item instanceof Ubicacion) {

                coincide = coincide ||
                        ((Ubicacion) item).getId()
                                .equalsIgnoreCase(valor.trim());
            }

            if (coincide) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private Object valorOpcional(String valor) {

        if (valor == null || valor.trim().isEmpty()) {
            return JSONObject.NULL;
        }

        return valor.trim();
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
