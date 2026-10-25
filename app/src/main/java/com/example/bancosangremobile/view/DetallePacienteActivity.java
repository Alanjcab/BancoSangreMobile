package com.example.bancosangremobile.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;

import com.example.bancosangremobile.repository.EvaluacionRepository;
import com.example.bancosangremobile.repository.DonacionRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.bancosangremobile.R;

import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import com.example.bancosangremobile.repository.RestriccionRepository;

import java.util.ArrayList;

import android.app.DatePickerDialog;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Locale;

import android.widget.Button;

public class DetallePacienteActivity extends AppCompatActivity {

    //pestañas
    private TextView tabInformacion;
    private TextView tabSalud;
    private TextView tabDonaciones;

    //secciones
    private View seccionInformacion;
    private View seccionSalud;
    private View seccionDonaciones;
    private DonacionesDonantePanel panelDonaciones;

    //colores
    private final int COLOR_ACTIVO = 0xFFF7065D;
    private final int COLOR_INACTIVO = 0xFF527080;
    private final EvaluacionRepository evaluacionRepository =
            new EvaluacionRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_paciente);

        findViewById(R.id.btnAgregarRestriccion).setOnClickListener(v -> {

            View formulario = getLayoutInflater().inflate(
                    R.layout.dialog_agregar_restriccion,
                    null
            );

            Spinner spinnerMotivos = formulario.findViewById(
                    R.id.spinnerMotivoRestriccion
            );

            View layoutOtroMotivo = formulario.findViewById(
                    R.id.layoutOtroMotivo
            );

            EditText etFechaDesde = formulario.findViewById(R.id.etFechaDesde);
            EditText etFechaHasta = formulario.findViewById(R.id.etFechaHasta);

            View.OnClickListener abrirCalendario = vista -> {

                EditText campo = (EditText) vista;
                Calendar calendario = Calendar.getInstance();

                DatePickerDialog selector = new DatePickerDialog(
                        this,
                        (datePicker, anio, mes, dia) -> {
                            String fecha = String.format(
                                    Locale.ROOT,
                                    "%04d-%02d-%02d",
                                    anio,
                                    mes + 1,
                                    dia
                            );

                            campo.setText(fecha);
                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                );

                selector.show();
            };

            etFechaDesde.setOnClickListener(abrirCalendario);
            etFechaHasta.setOnClickListener(abrirCalendario);

            spinnerMotivos.setEnabled(false);

            new Thread(() -> {
                try {
                    JSONArray motivos = new RestriccionRepository().listarMotivos();

                    ArrayList<String> nombres = new ArrayList<>();

                    for (int i = 0; i < motivos.length(); i++) {
                        nombres.add(
                                motivos.getJSONObject(i).getString("nombre")
                        );
                    }

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                nombres
                        );

                        adaptador.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                        );

                        spinnerMotivos.setAdapter(adaptador);
                        spinnerMotivos.setEnabled(true);

                        spinnerMotivos.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(
                                            AdapterView<?> parent,
                                            View view,
                                            int position,
                                            long id
                                    ) {
                                        String motivo = nombres.get(position);

                                        layoutOtroMotivo.setVisibility(
                                                motivo.equalsIgnoreCase("Otro")
                                                        ? View.VISIBLE
                                                        : View.GONE
                                        );
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                    });

                } catch (Exception e) {
                    runOnUiThread(() ->
                            Toast.makeText(
                                    this,
                                    "No se pudieron cargar los motivos: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show()
                    );
                }
            }).start();

            androidx.appcompat.app.AlertDialog dialogo =
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setView(formulario)
                            .create();

            formulario.findViewById(R.id.btnCancelarRestriccion)
                    .setOnClickListener(boton -> dialogo.dismiss());

            dialogo.show();


            formulario.findViewById(R.id.btnGuardarRestriccion)
                    .setOnClickListener(boton -> {

                        int idDonante = getIntent().getIntExtra("id_donante", 0);

                        Spinner spinner = formulario.findViewById(
                                R.id.spinnerMotivoRestriccion
                        );

                        EditText otroMotivo = formulario.findViewById(R.id.etOtroMotivo);
                        EditText fechaDesde = formulario.findViewById(R.id.etFechaDesde);
                        EditText fechaHasta = formulario.findViewById(R.id.etFechaHasta);
                        EditText observaciones = formulario.findViewById(
                                R.id.etObservacionesRestriccion
                        );

                        if (idDonante <= 0 || spinner.getSelectedItem() == null) {
                            Toast.makeText(this, "Faltan datos del donante o motivo",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String nombreMotivo = spinner.getSelectedItem().toString();
                        String nombrePersonalizado = otroMotivo.getText().toString().trim();
                        String desde = fechaDesde.getText().toString().trim();
                        String hasta = fechaHasta.getText().toString().trim();

                        boolean esOtro = nombreMotivo.equalsIgnoreCase("Otro");

                        if (esOtro && nombrePersonalizado.isEmpty()) {
                            otroMotivo.setError("Ingresá un motivo");
                            return;
                        }

                        if (desde.isEmpty()) {
                            fechaDesde.setError("Seleccioná la fecha de inicio");
                            Toast.makeText(this, "Seleccioná la fecha de inicio",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean permanente = ((android.widget.RadioButton)
                                formulario.findViewById(R.id.rbPermanente)).isChecked();

                        boolean bloqueante = ((android.widget.RadioButton)
                                formulario.findViewById(R.id.rbBloqueante)).isChecked();

                        if (!permanente && !hasta.isEmpty()
                                && hasta.compareTo(desde) < 0) {
                            Toast.makeText(this,
                                    "La fecha de finalización no puede ser anterior al inicio",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        View btnGuardar = formulario.findViewById(
                                R.id.btnGuardarRestriccion
                        );
                        btnGuardar.setEnabled(false);

                        new Thread(() -> {
                            try {
                                RestriccionRepository repository =
                                        new RestriccionRepository();

                                int idMotivo;

                                if (esOtro) {
                                    idMotivo = repository.crearMotivo(nombrePersonalizado);
                                } else {
                                    JSONArray motivos = repository.listarMotivos();
                                    idMotivo = 0;

                                    for (int i = 0; i < motivos.length(); i++) {
                                        JSONObject motivo = motivos.getJSONObject(i);

                                        if (nombreMotivo.equals(
                                                motivo.optString("nombre"))) {
                                            idMotivo = motivo.getInt(
                                                    "id_motivo_restriccion"
                                            );
                                            break;
                                        }
                                    }

                                    if (idMotivo <= 0) {
                                        throw new Exception("No se encontró el motivo");
                                    }
                                }

                                JSONObject datos = new JSONObject();
                                datos.put("id_donante", idDonante);
                                datos.put("id_motivo_restriccion", idMotivo);
                                datos.put("tipo",
                                        permanente ? "PERMANENTE" : "TEMPORAL");
                                datos.put("efecto",
                                        bloqueante ? "BLOQUEANTE" : "ADVERTENCIA");
                                datos.put("fecha_desde", desde);
                                datos.put("fecha_hasta_orientativa",
                                        permanente || hasta.isEmpty()
                                                ? JSONObject.NULL : hasta);
                                datos.put("observaciones",
                                        observaciones.getText().toString().trim());

                                repository.crearRestriccion(datos);

                                runOnUiThread(() -> {
                                    Toast.makeText(this,
                                            "Restricción guardada correctamente",
                                            Toast.LENGTH_SHORT).show();
                                    dialogo.dismiss();
                                    cargarRestricciones();
                                    cargarUltimaEvaluacion();
                                });

                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    btnGuardar.setEnabled(true);
                                    Toast.makeText(this,
                                            "Error al guardar: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });
                            }
                        }).start();
                    });

        });
        cargarDatosDonante();

        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        TextView btnEditar = findViewById(R.id.btnEditarDonante);

        btnEditar.setOnClickListener(v -> {

            int idDonante = getIntent().getIntExtra(
                    "id_donante", 0
            );

            if (idDonante <= 0) {
                Toast.makeText(
                        this,
                        "No se pudo identificar al donante",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Intent intent = new Intent(
                    DetallePacienteActivity.this,
                    NuevoDonanteActivity.class
            );

            intent.putExtra("modo_edicion", true);
            intent.putExtra("id_donante", idDonante);

            intent.putExtra(
                    "id_donante",
                    getIntent().getIntExtra("id_donante", 0)
            );
            startActivity(intent);
        });

        findViewById(R.id.btnEditarSalud).setOnClickListener(v -> {

            Intent intent = new Intent(
                    DetallePacienteActivity.this,
                    FichaTecnicaActivity.class
            );

            intent.putExtra(
                    "nombre",
                    getIntent().getStringExtra("nombre")
            );

            intent.putExtra(
                    "dni",
                    getIntent().getStringExtra("dni")
            );

            intent.putExtra(
                    "id_donante",
                    getIntent().getIntExtra("id_donante", 0)
            );

            startActivity(intent);
        });


        //referencia a las vistas
        tabInformacion = findViewById(R.id.tabInformacion);
        tabSalud = findViewById(R.id.tabSalud);
        tabDonaciones = findViewById(R.id.tabDonaciones);

        //referecnia a las secciones
        seccionInformacion =
                findViewById(R.id.seccionInformacion);

        seccionSalud =
                findViewById(R.id.seccionSalud);
        seccionDonaciones = findViewById(R.id.seccionDonaciones);
        panelDonaciones = new DonacionesDonantePanel(this);

        //evento de la vista
        tabInformacion.setOnClickListener(
                v -> mostrarSeccion(0)
        );

        tabSalud.setOnClickListener(
                v -> mostrarSeccion(1)
        );

        tabDonaciones.setOnClickListener(v -> {
            mostrarSeccion(2);
            panelDonaciones.cargar();
        });

        //vista inicial
        mostrarSeccion(0);

        //ajusto barra del sistema
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

    private void cargarDatosDonante() {

        //recibo los datos desde paciente activity
        String nombre = getIntent().getStringExtra("nombre");
        String dni = getIntent().getStringExtra("dni");
        String grupo = getIntent().getStringExtra("grupo");
        String aptitud = getIntent().getStringExtra("aptitud");

        //valores alternativo
        nombre = valorOTexto(nombre, "Sin nombre");
        dni = valorOTexto(dni, "Sin DNI");
        grupo = valorOTexto(grupo, "Sin datos");
        aptitud = valorOTexto(aptitud, "Sin evaluar");

        //encabezado
        TextView txtNombrePaciente =
                findViewById(R.id.txtNombrePaciente);

        TextView txtDniPaciente =
                findViewById(R.id.txtDniPaciente);

        TextView txtGrupoSanguineo =
                findViewById(R.id.txtGrupoSanguineo);

        txtNombrePaciente.setText(nombre);
        txtDniPaciente.setText("DNI " + dni);
        txtGrupoSanguineo.setText(grupo);

        //info persola
        TextView txtInfoNombre =
                findViewById(R.id.txtInfoNombre);

        TextView txtInfoDni =
                findViewById(R.id.txtInfoDni);

        TextView txtInfoGrupo =
                findViewById(R.id.txtInfoGrupo);

        txtInfoNombre.setText(nombre);
        txtInfoDni.setText(dni);
        txtInfoGrupo.setText(grupo);

        //apto
        TextView txtEstadoAptitud =
                findViewById(R.id.txtEstadoAptitud);

        switch (aptitud.toLowerCase(Locale.ROOT)) {

            case "apto":

                txtEstadoAptitud.setText(
                        "●  APTO PARA DONAR"
                );

                txtEstadoAptitud.setTextColor(
                        0xFF16875D
                );

                break;

            case "no apto":

                txtEstadoAptitud.setText(
                        "●  NO APTO PARA DONAR"
                );

                txtEstadoAptitud.setTextColor(
                        0xFFC62828
                );

                break;

            case "en evaluación":
            case "con restricción":

                txtEstadoAptitud.setText(
                        "●  " + aptitud.toUpperCase(Locale.ROOT)
                );

                txtEstadoAptitud.setTextColor(
                        0xFFE5A000
                );

                break;

            default:

                txtEstadoAptitud.setText(
                        "●  SIN EVALUAR"
                );

                txtEstadoAptitud.setTextColor(
                        0xFF527080
                );

                break;
        }

        // ÚLTIMA DONACIÓN
        // Se completará con información de MySQL.
        TextView txtUltimaDonacion =
                findViewById(R.id.txtUltimaDonacion);

        txtUltimaDonacion.setText("Sin datos");

        // PRÓXIMA DONACIÓN SUGERIDA
        TextView txtProximaDonacion =
                findViewById(R.id.txtProximaDonacion);

        txtProximaDonacion.setText("Sin datos");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarUltimaEvaluacion();
        cargarRestricciones();
        cargarFechasDonacion();
        if (panelDonaciones != null && panelDonaciones.haCargado()) {
            panelDonaciones.cargar();
        }
    }

    // consulta las donaciones reales cada vez que se vuelve a la ficha
    private void cargarFechasDonacion() {
        TextView ultimaVista = findViewById(R.id.txtUltimaDonacion);
        TextView proximaVista = findViewById(R.id.txtProximaDonacion);
        int idDonante = getIntent().getIntExtra("id_donante", 0);

        if (idDonante <= 0) {
            ultimaVista.setText("Sin datos");
            proximaVista.setText("Sin datos");
            return;
        }

        ultimaVista.setText("Cargando...");
        proximaVista.setText("Cargando...");

        new Thread(() -> {
            try {
                JSONArray donaciones = new DonacionRepository().listarDonaciones();
                JSONObject masReciente = null;
                String fechaReciente = "";

                for (int i = 0; i < donaciones.length(); i++) {
                    JSONObject donacion = donaciones.optJSONObject(i);
                    if (donacion == null || donacion.optInt("id_donante", 0) != idDonante) {
                        continue;
                    }
                    String fecha = donacion.optString("fecha_donacion", "");
                    if (fecha.length() < 10) continue;
                    fecha = fecha.substring(0, 10);
                    if (!fecha.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) continue;

                    if (masReciente == null || fecha.compareTo(fechaReciente) > 0 ||
                            (fecha.equals(fechaReciente) &&
                                    donacion.optInt("id_donacion", 0) >
                                            masReciente.optInt("id_donacion", 0))) {
                        masReciente = donacion;
                        fechaReciente = fecha;
                    }
                }

                final String ultimaTexto;
                final String proximaTexto;
                if (masReciente == null) {
                    ultimaTexto = "Sin donaciones registradas";
                    proximaTexto = "Sin datos";
                } else {
                    java.text.SimpleDateFormat formatoEntrada =
                            new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                    formatoEntrada.setLenient(false);
                    java.util.Date fecha = formatoEntrada.parse(fechaReciente);
                    java.text.SimpleDateFormat formatoSalida =
                            new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.ROOT);
                    ultimaTexto = formatoSalida.format(fecha);

                    //2 meses se aplica a sangre entera
                    if (masReciente.optInt("id_tipo_donacion", 0) == 1) {
                        Calendar calendario = Calendar.getInstance();
                        calendario.setTime(fecha);
                        calendario.add(Calendar.MONTH, 2);
                        proximaTexto = formatoSalida.format(calendario.getTime())
                                + " (orientativa)";
                    } else {
                        proximaTexto = "A definir según modalidad y evaluación médica";
                    }
                }

                runOnUiThread(() -> {
                    ultimaVista.setText(ultimaTexto);
                    proximaVista.setText(proximaTexto);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    ultimaVista.setText("No disponible");
                    proximaVista.setText("No disponible");
                    Toast.makeText(this,
                            "No se pudieron consultar las donaciones: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void mostrarSeccion(int seleccion) {

        View[] secciones = {
                seccionInformacion,
                seccionSalud,
                seccionDonaciones
        };

        TextView[] pestanas = {
                tabInformacion,
                tabSalud,
                tabDonaciones
        };

        for (int i = 0; i < secciones.length; i++) {

            boolean activa = i == seleccion;

            secciones[i].setVisibility(
                    activa ? View.VISIBLE : View.GONE
            );

            pestanas[i].setTextColor(
                    activa ? COLOR_ACTIVO : COLOR_INACTIVO
            );

            pestanas[i].setTypeface(
                    null,
                    activa
                            ? android.graphics.Typeface.BOLD
                            : android.graphics.Typeface.NORMAL
            );
        }
    }


    private void cargarUltimaEvaluacion() {

        int idDonante = getIntent().getIntExtra("id_donante", 0);

        if (idDonante <= 0) {
            return;
        }

        new Thread(() -> {

            try {
                JSONArray evaluaciones =
                        evaluacionRepository.listarEvaluaciones(idDonante);

                JSONObject ultima = null;

                // busco la evaluacion mas reciente
                for (int i = 0; i < evaluaciones.length(); i++) {

                    JSONObject actual = evaluaciones.getJSONObject(i);

                    if (ultima == null ||
                            actual.optString("fecha_evaluacion", "")
                                    .compareTo(
                                            ultima.optString("fecha_evaluacion", "")
                                    ) > 0 ||
                            (actual.optString("fecha_evaluacion", "")
                                    .equals(ultima.optString("fecha_evaluacion", ""))
                                    && actual.optInt("id_evaluacion", 0)
                                    > ultima.optInt("id_evaluacion", 0))) {

                        ultima = actual;
                    }
                }

                JSONObject resultado = ultima;
                JSONArray restricciones = new RestriccionRepository()
                        .listarRestricciones(idDonante);

                runOnUiThread(() -> {

                    if (resultado == null) {
                        mostrarSaludSinDatos();

                        String estado = calcularEstadoDonante(
                                "",
                                restricciones
                        );

                        TextView txtEstado = findViewById(R.id.txtEstadoAptitud);

                        if ("NO_HABILITADO".equals(estado)) {
                            txtEstado.setText("●  NO HABILITADO");
                            txtEstado.setTextColor(0xFFC62828);

                        } else if ("ADVERTENCIA".equals(estado)) {
                            txtEstado.setText("●  ADVERTENCIA");
                            txtEstado.setTextColor(0xFFE5A000);
                        }

                        return;
                    }

                    mostrarHistorialEvaluaciones(
                            evaluaciones,
                            resultado.optInt("id_evaluacion", 0)
                    );

                    String fecha = resultado.optString(
                            "fecha_evaluacion", ""
                    );

                    if (fecha.length() >= 10) {
                        String[] partes = fecha.substring(0, 10).split("-");

                        if (partes.length == 3) {
                            fecha = partes[2] + "/" + partes[1]
                                    + "/" + partes[0];
                        }
                    }

                    ((TextView) findViewById(
                            R.id.txtFechaUltimaEvaluacion
                    )).setText("Fecha: " + fecha);

                    String hematocrito = valorEvaluacion(
                            resultado, "hematocrito"
                    );

                    ((TextView) findViewById(
                            R.id.txtSaludHematocrito
                    )).setText(
                            hematocrito.equals("Sin datos")
                                    ? hematocrito
                                    : hematocrito + " %"
                    );

                    String plaquetas = valorEvaluacion(
                            resultado, "recuento_plaquetario"
                    );

                    if (!plaquetas.equals("Sin datos")) {
                        try {
                            double cantidad = Double.parseDouble(plaquetas);
                            plaquetas = String.format(
                                    Locale.getDefault(),
                                    "%.0f",
                                    cantidad
                            );
                        } catch (NumberFormatException e) {
                            plaquetas = "Sin datos";
                        }
                    }

                    ((TextView) findViewById(
                            R.id.txtSaludPlaquetas
                    )).setText(plaquetas);

                    String peso = valorEvaluacion(
                            resultado, "peso_kg"
                    );

                    ((TextView) findViewById(
                            R.id.txtSaludPeso
                    )).setText(
                            peso.equals("Sin datos")
                                    ? peso
                                    : peso + " kg"
                    );

                    String observaciones = valorEvaluacion(
                            resultado, "observaciones"
                    );

                    ((TextView) findViewById(
                            R.id.txtSaludObservaciones
                    )).setText(
                            observaciones.equals("Sin datos")
                                    ? "Sin observaciones registradas."
                                    : observaciones
                    );

                    TextView txtEstado = findViewById(
                            R.id.txtEstadoAptitud
                    );

                    String estado = calcularEstadoDonante(
                            resultado.optString("estado_aptitud", ""),
                            restricciones
                    );

                    switch (estado) {
                        case "HABILITADO":
                            txtEstado.setText("●  HABILITADO");
                            txtEstado.setTextColor(0xFF16875D);
                            break;

                        case "ADVERTENCIA":
                            txtEstado.setText("●  ADVERTENCIA");
                            txtEstado.setTextColor(0xFFE5A000);
                            break;

                        case "NO_HABILITADO":
                            txtEstado.setText("●  NO HABILITADO");
                            txtEstado.setTextColor(0xFFC62828);
                            break;

                        default:
                            txtEstado.setText("●  SIN EVALUAR");
                            txtEstado.setTextColor(0xFF527080);
                            break;
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "No se pudieron cargar las evaluaciones: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }

        }).start();
    }


    private String calcularEstadoDonante(
            String estadoEvaluacion,
            JSONArray restricciones) {

        boolean tieneAdvertencia = false;

        for (int i = 0; i < restricciones.length(); i++) {

            JSONObject restriccion = restricciones.optJSONObject(i);

            if (restriccion == null ||
                    !"ACTIVA".equalsIgnoreCase(
                            restriccion.optString("estado"))) {
                continue;
            }

            String efecto = restriccion.optString("efecto");

            if ("BLOQUEANTE".equalsIgnoreCase(efecto)) {
                return "NO_HABILITADO";
            }

            if ("ADVERTENCIA".equalsIgnoreCase(efecto)) {
                tieneAdvertencia = true;
            }
        }

        if ("NO_HABILITADO".equals(estadoEvaluacion)) {
            return "NO_HABILITADO";
        }

        if ("ADVERTENCIA".equals(estadoEvaluacion)
                || tieneAdvertencia) {
            return "ADVERTENCIA";
        }

        return estadoEvaluacion;
    }


    private String valorEvaluacion(JSONObject datos, String campo) {

        if (datos.isNull(campo)) {
            return "Sin datos";
        }

        String valor = datos.optString(campo, "").trim();

        return valor.isEmpty() || valor.equalsIgnoreCase("null")
                ? "Sin datos"
                : valor;
    }

    private void mostrarSaludSinDatos() {

        ((TextView) findViewById(
                R.id.txtFechaUltimaEvaluacion
        )).setText("Sin evaluaciones registradas");

        ((TextView) findViewById(
                R.id.txtSaludHematocrito
        )).setText("Sin datos");

        ((TextView) findViewById(
                R.id.txtSaludPlaquetas
        )).setText("Sin datos");

        ((TextView) findViewById(
                R.id.txtSaludPeso
        )).setText("Sin datos");

        ((TextView) findViewById(
                R.id.txtSaludObservaciones
        )).setText("Sin observaciones registradas.");

        TextView txtEstado = findViewById(R.id.txtEstadoAptitud);
        txtEstado.setText("●  SIN EVALUAR");
        txtEstado.setTextColor(0xFF527080);
    }


    private void mostrarHistorialEvaluaciones(
            JSONArray evaluaciones,
            int idUltimaEvaluacion
    ) {

        LinearLayout contenedor = findViewById(
                R.id.layoutHistorialEvaluaciones
        );

        TextView txtSinHistorial = findViewById(
                R.id.txtSinHistorialEvaluaciones
        );

        android.widget.Button btnVerTodas = findViewById(
                R.id.btnVerTodasEvaluaciones
        );

        contenedor.removeAllViews();
        btnVerTodas.setVisibility(View.GONE);
        btnVerTodas.setOnClickListener(null);

        java.util.ArrayList<JSONObject> anteriores =
                new java.util.ArrayList<>();

        for (int i = 0; i < evaluaciones.length(); i++) {

            JSONObject evaluacion = evaluaciones.optJSONObject(i);

            if (evaluacion != null &&
                    evaluacion.optInt("id_evaluacion", 0)
                            != idUltimaEvaluacion) {

                anteriores.add(evaluacion);
            }
        }

        //ordeno desde la mas reciente
        anteriores.sort((a, b) -> {

            int comparacion = b.optString("fecha_evaluacion", "")
                    .compareTo(a.optString("fecha_evaluacion", ""));

            if (comparacion != 0) {
                return comparacion;
            }

            return Integer.compare(
                    b.optInt("id_evaluacion", 0),
                    a.optInt("id_evaluacion", 0)
            );
        });

        if (anteriores.isEmpty()) {
            txtSinHistorial.setVisibility(View.VISIBLE);
            contenedor.setVisibility(View.GONE);
            return;
        }

        txtSinHistorial.setVisibility(View.GONE);
        contenedor.setVisibility(View.VISIBLE);

        //creo la tarjeta por cada evaluaacion anterior
        for (int i = 0; i < anteriores.size(); i++) {

            JSONObject evaluacion = anteriores.get(i);

            LinearLayout tarjeta = new LinearLayout(this);
            tarjeta.setOrientation(LinearLayout.VERTICAL);

            int paddingHorizontal = (int) (16 * getResources()
                    .getDisplayMetrics().density);

            int paddingVertical = (int) (12 * getResources()
                    .getDisplayMetrics().density);

            tarjeta.setPadding(
                    paddingHorizontal,
                    paddingVertical,
                    paddingHorizontal,
                    paddingVertical
            );

            tarjeta.setBackgroundResource(R.drawable.bg_dato_celeste);

            LinearLayout.LayoutParams parametros =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            parametros.bottomMargin = (int) (10 * getResources()
                    .getDisplayMetrics().density);

            contenedor.addView(tarjeta, parametros);

            //la primera evaluación anterior siempre se muestra las otras quedan ocultsa
            if (i > 0) {
                tarjeta.setVisibility(View.GONE);
            }

            String fecha = evaluacion.optString(
                    "fecha_evaluacion", ""
            );

            if (fecha.length() >= 10) {
                String[] partes = fecha.substring(0, 10).split("-");

                if (partes.length == 3) {
                    fecha = partes[2] + "/" + partes[1]
                            + "/" + partes[0];
                }
            }

            TextView txtFecha = new TextView(this);
            txtFecha.setText(fecha);
            txtFecha.setTextSize(17);
            txtFecha.setTextColor(0xFF163B4A);
            txtFecha.setTypeface(null, Typeface.BOLD);
            tarjeta.addView(txtFecha);

            String estado = evaluacion.optString(
                    "estado_aptitud", ""
            );

            String estadoVisible;

            switch (estado) {
                case "HABILITADO":
                    estadoVisible = "Habilitado";
                    break;

                case "ADVERTENCIA":
                    estadoVisible = "Advertencia";
                    break;

                case "NO_HABILITADO":
                    estadoVisible = "No habilitado";
                    break;

                default:
                    estadoVisible = "Sin evaluar";
            }

            TextView txtEstado = new TextView(this);
            txtEstado.setText("Estado: " + estadoVisible);
            txtEstado.setTextSize(14);
            txtEstado.setTextColor(0xFF527080);
            tarjeta.addView(txtEstado);

            String hematocrito = valorEvaluacion(
                    evaluacion, "hematocrito"
            );

            String plaquetas = valorEvaluacion(
                    evaluacion, "recuento_plaquetario"
            );

            String peso = valorEvaluacion(
                    evaluacion, "peso_kg"
            );

            TextView txtDatos = new TextView(this);

            txtDatos.setText(
                    "Hematocrito: " + hematocrito
                            + (hematocrito.equals("Sin datos") ? "" : " %")
                            + "\nPlaquetas: " + plaquetas
                            + (plaquetas.equals("Sin datos") ? "" : "")
                            + "\nPeso: " + peso
                            + (peso.equals("Sin datos") ? "" : " kg")
            );

            txtDatos.setTextSize(14);
            txtDatos.setTextColor(0xFF163B4A);
            txtDatos.setPadding(0, paddingVertical, 0, 0);

            tarjeta.addView(txtDatos);

            String observaciones = valorEvaluacion(
                    evaluacion, "observaciones"
            );

            if (!observaciones.equals("Sin datos")) {

                TextView txtObservaciones = new TextView(this);

                txtObservaciones.setText(
                        "Observaciones: " + observaciones
                );

                txtObservaciones.setTextSize(14);
                txtObservaciones.setTextColor(0xFF527080);
                txtObservaciones.setPadding(
                        0, paddingVertical / 2, 0, 0
                );

                tarjeta.addView(txtObservaciones);
            }
        }

        // Mostrar el botón solamente si hay más de una
        // evaluación anterior.
        if (anteriores.size() > 1) {

            btnVerTodas.setVisibility(View.VISIBLE);
            btnVerTodas.setText("Ver todas las evaluaciones ▼");

            final boolean[] expandido = {false};

            btnVerTodas.setOnClickListener(v -> {

                expandido[0] = !expandido[0];

                for (int i = 1; i < contenedor.getChildCount(); i++) {

                    contenedor.getChildAt(i).setVisibility(
                            expandido[0] ? View.VISIBLE : View.GONE
                    );
                }

                btnVerTodas.setText(
                        expandido[0]
                                ? "Ocultar evaluaciones ▲"
                                : "Ver todas las evaluaciones ▼"
                );
            });
        }
    }


    private void cargarRestricciones() {
        int idDonante = getIntent().getIntExtra("id_donante", 0);

        if (idDonante <= 0) {
            return;
        }

        LinearLayout contenedor = findViewById(R.id.layoutRestricciones);
        LinearLayout historial = findViewById(R.id.layoutHistorialRestricciones);
        LinearLayout seccionHistorial = findViewById(R.id.seccionHistorialRestricciones);
        Button btnHistorial = findViewById(R.id.btnVerHistorialRestricciones);
        TextView txtSinRestricciones = findViewById(R.id.txtSinRestricciones);

        new Thread(() -> {
            try {
                JSONArray restricciones =
                        new RestriccionRepository()
                                .listarRestricciones(idDonante);

                runOnUiThread(() -> {
                    contenedor.removeAllViews();
                    historial.removeAllViews();
                    txtSinRestricciones.setVisibility(View.GONE);
                    contenedor.setVisibility(View.VISIBLE);

                    for (int i = 0; i < restricciones.length(); i++) {
                        JSONObject restriccion =
                                restricciones.optJSONObject(i);

                        if (restriccion == null) {
                            continue;
                        }

                        String motivo = restriccion.optString("motivo");
                        String tipo = restriccion.optString("tipo");
                        String efecto = restriccion.optString("efecto");
                        String estado = restriccion.optString("estado");
                        String desde = restriccion.optString("fecha_desde");
                        String hasta = restriccion.optString(
                                "fecha_hasta_orientativa", ""
                        );

                        LinearLayout tarjeta = new LinearLayout(this);
                        tarjeta.setOrientation(LinearLayout.VERTICAL);
                        tarjeta.setPadding(24, 20, 24, 20);
                        tarjeta.setBackgroundColor(
                                android.graphics.Color.rgb(245, 245, 245)
                        );

                        TextView titulo = new TextView(this);
                        titulo.setText(motivo);
                        titulo.setTextSize(17);
                        titulo.setTypeface(null, android.graphics.Typeface.BOLD);

                        TextView detalle = new TextView(this);

                        String texto = "Tipo: " + tipo
                                + "\nEfecto: " + efecto
                                + "\nEstado: " + estado
                                + "\nDesde: " + desde;

                        if (!hasta.isEmpty() && !hasta.equals("null")) {
                            texto += "\nHasta (orientativa): " + hasta;
                        }

                        detalle.setText(texto);
                        detalle.setTextSize(14);
                        detalle.setPadding(0, 8, 0, 0);

                        tarjeta.addView(titulo);
                        tarjeta.addView(detalle);


                        if ("ACTIVA".equalsIgnoreCase(estado)) {

                            Button btnEditar = new Button(this);
                            btnEditar.setText("Editar restricción");
                            btnEditar.setAllCaps(false);

                            LinearLayout.LayoutParams paramsEditar =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );

                            paramsEditar.topMargin = 12;

                            tarjeta.addView(btnEditar, paramsEditar);

                            btnEditar.setOnClickListener(v -> {

                                View formulario = getLayoutInflater().inflate(
                                        R.layout.dialog_agregar_restriccion,
                                        null
                                );

                                androidx.appcompat.app.AlertDialog dialogo =
                                        new androidx.appcompat.app.AlertDialog.Builder(this)
                                                .setTitle("Editar restricción")
                                                .setView(formulario)
                                                .create();

                                EditText etFechaDesde =
                                        formulario.findViewById(R.id.etFechaDesde);

                                EditText etFechaHasta =
                                        formulario.findViewById(R.id.etFechaHasta);

                                EditText etObservaciones =
                                        formulario.findViewById(R.id.etObservacionesRestriccion);

                                etFechaDesde.setText(
                                        restriccion.optString("fecha_desde", "")
                                );

                                String fechaHasta = restriccion.optString(
                                        "fecha_hasta_orientativa", ""
                                );

                                etFechaHasta.setText(
                                        "null".equalsIgnoreCase(fechaHasta) ? "" : fechaHasta
                                );

                                View layoutFechaHastaEdicion = formulario.findViewById(R.id.layoutFechaHasta);
                                android.widget.RadioGroup grupoTipoEdicion = formulario.findViewById(R.id.grupoTipoRestriccion);
                                layoutFechaHastaEdicion.setVisibility(
                                        "PERMANENTE".equalsIgnoreCase(tipo) ? View.GONE : View.VISIBLE
                                );
                                grupoTipoEdicion.setOnCheckedChangeListener((grupo, checkedId) ->
                                        layoutFechaHastaEdicion.setVisibility(
                                                checkedId == R.id.rbPermanente ? View.GONE : View.VISIBLE
                                        )
                                );


                                View.OnClickListener abrirCalendarioEdicion = vista -> {
                                    EditText campo = (EditText) vista;
                                    Calendar calendario = Calendar.getInstance();

                                    DatePickerDialog selector = new DatePickerDialog(
                                            this,
                                            (datePicker, anio, mes, dia) -> {
                                                String fecha = String.format(
                                                        Locale.ROOT,
                                                        "%04d-%02d-%02d",
                                                        anio, mes + 1, dia
                                                );
                                                campo.setText(fecha);
                                            },
                                            calendario.get(Calendar.YEAR),
                                            calendario.get(Calendar.MONTH),
                                            calendario.get(Calendar.DAY_OF_MONTH)
                                    );

                                    selector.show();
                                };

                                etFechaDesde.setOnClickListener(abrirCalendarioEdicion);
                                etFechaHasta.setOnClickListener(abrirCalendarioEdicion);


                                etObservaciones.setText(
                                        restriccion.optString("observaciones", "")
                                );

                                ((android.widget.RadioButton) formulario.findViewById(
                                        "PERMANENTE".equalsIgnoreCase(tipo)
                                                ? R.id.rbPermanente
                                                : R.id.rbTemporal
                                )).setChecked(true);

                                ((android.widget.RadioButton) formulario.findViewById(
                                        "BLOQUEANTE".equalsIgnoreCase(efecto)
                                                ? R.id.rbBloqueante
                                                : R.id.rbAdvertencia
                                )).setChecked(true);

                                formulario.findViewById(R.id.btnCancelarRestriccion)
                                        .setOnClickListener(b -> dialogo.dismiss());


                                Spinner spinnerEdicion = formulario.findViewById(
                                        R.id.spinnerMotivoRestriccion
                                );

                                View layoutOtroEdicion = formulario.findViewById(
                                        R.id.layoutOtroMotivo
                                );

                                spinnerEdicion.setEnabled(false);

                                new Thread(() -> {
                                    try {
                                        JSONArray motivos = new RestriccionRepository().listarMotivos();
                                        ArrayList<String> nombres = new ArrayList<>();

                                        int idMotivoActual = restriccion.optInt(
                                                "id_motivo_restriccion", 0
                                        );

                                        int posicionActual = -1;

                                        for (int j = 0; j < motivos.length(); j++) {
                                            JSONObject motivoSeleccion = motivos.getJSONObject(j);

                                            nombres.add(motivoSeleccion.getString("nombre"));

                                            if (motivoSeleccion.getInt("id_motivo_restriccion")
                                                    == idMotivoActual) {
                                                posicionActual = j;
                                            }
                                        }

                                        int seleccion = posicionActual;

                                        runOnUiThread(() -> {
                                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                                                    this,
                                                    android.R.layout.simple_spinner_item,
                                                    nombres
                                            );

                                            adaptador.setDropDownViewResource(
                                                    android.R.layout.simple_spinner_dropdown_item
                                            );

                                            spinnerEdicion.setAdapter(adaptador);

                                            if (seleccion >= 0) {
                                                spinnerEdicion.setSelection(seleccion);
                                            }

                                            spinnerEdicion.setEnabled(true);

                                            spinnerEdicion.setOnItemSelectedListener(
                                                    new AdapterView.OnItemSelectedListener() {
                                                        @Override
                                                        public void onItemSelected(
                                                                AdapterView<?> parent,
                                                                View view,
                                                                int position,
                                                                long id) {

                                                            String nombre = nombres.get(position);

                                                            layoutOtroEdicion.setVisibility(
                                                                    nombre.equalsIgnoreCase("Otro")
                                                                            ? View.VISIBLE
                                                                            : View.GONE
                                                            );
                                                        }

                                                        @Override
                                                        public void onNothingSelected(
                                                                AdapterView<?> parent) {
                                                        }
                                                    }
                                            );
                                        });

                                    } catch (Exception e) {
                                        runOnUiThread(() -> Toast.makeText(
                                                this,
                                                "Error al cargar motivos: " + e.getMessage(),
                                                Toast.LENGTH_LONG
                                        ).show());
                                    }
                                }).start();
                                dialogo.show();


                                Button btnGuardarEdicion = formulario.findViewById(
                                        R.id.btnGuardarRestriccion
                                );

                                btnGuardarEdicion.setText("Guardar cambios");

                                btnGuardarEdicion.setOnClickListener(b -> {

                                    int idRestriccion = restriccion.optInt("id_restriccion", 0);

                                    if (idRestriccion <= 0 || !spinnerEdicion.isEnabled()
                                            || spinnerEdicion.getSelectedItem() == null) {
                                        Toast.makeText(this, "Esperá a que carguen los datos",
                                                Toast.LENGTH_SHORT).show();
                                        return;
                                    }

                                    String nombreMotivo = spinnerEdicion.getSelectedItem().toString();
                                    String nombreOtro = ((EditText) formulario.findViewById(
                                            R.id.etOtroMotivo)).getText().toString().trim();

                                    String fechaDesde = etFechaDesde.getText().toString().trim();

                                    String fechaHastaEditada = etFechaHasta.getText().toString().trim();

                                    boolean permanente = ((android.widget.RadioButton)
                                            formulario.findViewById(R.id.rbPermanente)).isChecked();

                                    boolean bloqueante = ((android.widget.RadioButton)
                                            formulario.findViewById(R.id.rbBloqueante)).isChecked();

                                    if (nombreMotivo.equalsIgnoreCase("Otro") && nombreOtro.isEmpty()) {
                                        ((EditText) formulario.findViewById(R.id.etOtroMotivo))
                                                .setError("Ingresá un motivo");
                                        return;
                                    }

                                    if (fechaDesde.isEmpty()) {
                                        etFechaDesde.setError("Seleccioná una fecha");
                                        return;
                                    }

                                    if (!permanente && !fechaHastaEditada.isEmpty()
                                            && fechaHastaEditada.compareTo(fechaDesde) < 0) {
                                        Toast.makeText(this,
                                                "La fecha de finalización no puede ser anterior al inicio",
                                                Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    String observacionesEditadas = etObservaciones.getText().toString().trim();
                                    btnGuardarEdicion.setEnabled(false);

                                    new Thread(() -> {
                                        try {
                                            RestriccionRepository repository = new RestriccionRepository();
                                            int idMotivo = 0;

                                            if (nombreMotivo.equalsIgnoreCase("Otro")) {
                                                idMotivo = repository.crearMotivo(nombreOtro);
                                            } else {
                                                JSONArray motivos = repository.listarMotivos();

                                                for (int j = 0; j < motivos.length(); j++) {
                                                    JSONObject motivoDisponible = motivos.getJSONObject(j);

                                                    if (nombreMotivo.equals(
                                                            motivoDisponible.optString("nombre"))) {
                                                        idMotivo = motivoDisponible.getInt(
                                                                "id_motivo_restriccion");
                                                        break;
                                                    }
                                                }
                                            }

                                            if (idMotivo <= 0) {
                                                throw new Exception("No se encontró el motivo");
                                            }

                                            JSONObject datos = new JSONObject();
                                            datos.put("id_restriccion", idRestriccion);
                                            datos.put("id_motivo_restriccion", idMotivo);
                                            datos.put("tipo", permanente ? "PERMANENTE" : "TEMPORAL");
                                            datos.put("efecto", bloqueante ? "BLOQUEANTE" : "ADVERTENCIA");
                                            datos.put("fecha_desde", fechaDesde);
                                            datos.put("fecha_hasta_orientativa",
                                                    permanente || fechaHastaEditada.isEmpty()
                                                            ? JSONObject.NULL : fechaHastaEditada);
                                            datos.put("observaciones",
                                                    observacionesEditadas);

                                            repository.editarRestriccion(datos);

                                            runOnUiThread(() -> {
                                                Toast.makeText(this, "Restricción actualizada",
                                                        Toast.LENGTH_SHORT).show();
                                                dialogo.dismiss();
                                                cargarRestricciones();
                                                cargarUltimaEvaluacion();
                                            });

                                        } catch (Exception e) {
                                            runOnUiThread(() -> {
                                                btnGuardarEdicion.setEnabled(true);
                                                Toast.makeText(this,
                                                        "Error al editar: " + e.getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            });
                                        }
                                    }).start();
                                });

                            });

                            Button btnFinalizar = new Button(this);
                            btnFinalizar.setText("Finalizar restricción");
                            btnFinalizar.setAllCaps(false);

                            LinearLayout.LayoutParams paramsBoton =
                                    new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );

                            paramsBoton.topMargin = 12;

                            tarjeta.addView(btnFinalizar, paramsBoton);

                            btnFinalizar.setOnClickListener(v -> {

                                new androidx.appcompat.app.AlertDialog.Builder(this)
                                        .setTitle("Finalizar restricción")
                                        .setMessage(
                                                "¿Confirmás que esta restricción médica " +
                                                        "debe finalizarse? Esta acción quedará " +
                                                        "registrada en su historial."
                                        )
                                        .setNegativeButton("Cancelar", null)
                                        .setPositiveButton("Finalizar", (dialog, which) -> {

                                            int idRestriccion =
                                                    restriccion.optInt("id_restriccion", 0);

                                            if (idRestriccion <= 0) {
                                                Toast.makeText(this,
                                                        "ID de restricción inválido",
                                                        Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            btnFinalizar.setEnabled(false);

                                            new Thread(() -> {
                                                try {
                                                    new RestriccionRepository()
                                                            .finalizarRestriccion(idRestriccion);

                                                    runOnUiThread(() -> {
                                                        Toast.makeText(this,
                                                                "Restricción finalizada",
                                                                Toast.LENGTH_SHORT).show();

                                                        cargarRestricciones();
                                                        cargarUltimaEvaluacion();
                                                    });

                                                } catch (Exception e) {
                                                    runOnUiThread(() -> {
                                                        btnFinalizar.setEnabled(true);

                                                        Toast.makeText(this,
                                                                "Error: " + e.getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    });
                                                }
                                            }).start();
                                        })
                                        .show();
                            });
                        }


                        LinearLayout.LayoutParams parametros =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );

                        parametros.bottomMargin = 16;

                        if ("ACTIVA".equalsIgnoreCase(estado)) {
                            contenedor.addView(tarjeta, parametros);
                        } else {
                            historial.addView(tarjeta, parametros);
                        }
                    }


                    if (contenedor.getChildCount() == 0) {
                        txtSinRestricciones.setText("Sin restricciones activas.");
                        txtSinRestricciones.setVisibility(View.VISIBLE);
                        contenedor.setVisibility(View.GONE);
                    } else {
                        txtSinRestricciones.setVisibility(View.GONE);
                        contenedor.setVisibility(View.VISIBLE);
                    }

                    if (historial.getChildCount() > 0) {
                        seccionHistorial.setVisibility(View.VISIBLE);

                        btnHistorial.setText("Ver historial de restricciones ▼");
                        historial.setVisibility(View.GONE);

                        btnHistorial.setOnClickListener(v -> {
                            boolean expandido =
                                    historial.getVisibility() == View.VISIBLE;

                            historial.setVisibility(
                                    expandido ? View.GONE : View.VISIBLE
                            );

                            btnHistorial.setText(
                                    expandido
                                            ? "Ver historial de restricciones ▼"
                                            : "Ocultar historial de restricciones ▲"
                            );
                        });

                    } else {
                        seccionHistorial.setVisibility(View.GONE);
                    }

                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Error al cargar restricciones: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }
        }).start();
    }

    private String valorOTexto(
            String valor,
            String alternativa
    ) {

        if (valor == null || valor.trim().isEmpty()) {
            return alternativa;
        }

        return valor;
    }
}