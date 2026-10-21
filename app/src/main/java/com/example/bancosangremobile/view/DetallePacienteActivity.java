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
import org.json.JSONArray;
import org.json.JSONObject;
import com.example.bancosangremobile.R;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import android.view.Gravity;

import java.util.Locale;

public class DetallePacienteActivity extends AppCompatActivity {

    // PESTAÑAS
    private TextView tabInformacion;
    private TextView tabSalud;
    private TextView tabTurnos;
    private TextView tabDonaciones;

    // SECCIONES
    private View seccionInformacion;
    private View seccionSalud;
    private View seccionTurnos;
    private View seccionDonaciones;

    // COLORES
    private final int COLOR_ACTIVO = 0xFFF7065D;
    private final int COLOR_INACTIVO = 0xFF527080;
    private final EvaluacionRepository evaluacionRepository =
            new EvaluacionRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_paciente);

        // CARGAR DATOS DEL DONANTE SELECCIONADO
        cargarDatosDonante();

        // BOTÓN VOLVER
        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        // BOTÓN EDITAR DONANTE
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



        // REFERENCIAS A LAS PESTAÑAS
        tabInformacion = findViewById(R.id.tabInformacion);
        tabSalud = findViewById(R.id.tabSalud);
        tabTurnos = findViewById(R.id.tabTurnos);
        tabDonaciones = findViewById(R.id.tabDonaciones);

        // REFERENCIAS A LAS SECCIONES
        seccionInformacion =
                findViewById(R.id.seccionInformacion);

        seccionSalud =
                findViewById(R.id.seccionSalud);

        seccionTurnos =
                findViewById(R.id.seccionTurnos);

        seccionDonaciones =
                findViewById(R.id.seccionDonaciones);

        // EVENTOS DE LAS PESTAÑAS
        tabInformacion.setOnClickListener(
                v -> mostrarSeccion(0)
        );

        tabSalud.setOnClickListener(
                v -> mostrarSeccion(1)
        );

        tabTurnos.setOnClickListener(
                v -> mostrarSeccion(2)
        );

        tabDonaciones.setOnClickListener(
                v -> mostrarSeccion(3)
        );

        // PESTAÑA INICIAL
        mostrarSeccion(0);

        // AJUSTES DE BARRAS DEL SISTEMA
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

        // RECIBIR DATOS DESDE PACIENTES ACTIVITY
        String nombre = getIntent().getStringExtra("nombre");
        String dni = getIntent().getStringExtra("dni");
        String grupo = getIntent().getStringExtra("grupo");
        String aptitud = getIntent().getStringExtra("aptitud");

        // VALORES ALTERNATIVOS
        nombre = valorOTexto(nombre, "Sin nombre");
        dni = valorOTexto(dni, "Sin DNI");
        grupo = valorOTexto(grupo, "Sin datos");
        aptitud = valorOTexto(aptitud, "Sin evaluar");

        // ENCABEZADO
        TextView txtNombrePaciente =
                findViewById(R.id.txtNombrePaciente);

        TextView txtDniPaciente =
                findViewById(R.id.txtDniPaciente);

        TextView txtGrupoSanguineo =
                findViewById(R.id.txtGrupoSanguineo);

        txtNombrePaciente.setText(nombre);
        txtDniPaciente.setText("DNI " + dni);
        txtGrupoSanguineo.setText(grupo);

        // INFORMACIÓN PERSONAL
        TextView txtInfoNombre =
                findViewById(R.id.txtInfoNombre);

        TextView txtInfoDni =
                findViewById(R.id.txtInfoDni);

        TextView txtInfoGrupo =
                findViewById(R.id.txtInfoGrupo);

        txtInfoNombre.setText(nombre);
        txtInfoDni.setText(dni);
        txtInfoGrupo.setText(grupo);

        // ESTADO DE APTITUD
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

        // TURNOS
        // Ocultar tarjeta de ejemplo hasta conectar la API.
        findViewById(R.id.layoutTurnosProximos)
                .setVisibility(View.GONE);

        findViewById(R.id.layoutSinTurnos)
                .setVisibility(View.VISIBLE);

        // DONACIONES
        // Ocultar historial ficticio hasta conectar la API.
        findViewById(R.id.layoutDonacionesRegistradas)
                .setVisibility(View.GONE);

        findViewById(R.id.layoutSinDonaciones)
                .setVisibility(View.VISIBLE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        cargarUltimaEvaluacion();
    }

    private void mostrarSeccion(int seleccion) {

        View[] secciones = {
                seccionInformacion,
                seccionSalud,
                seccionTurnos,
                seccionDonaciones
        };

        TextView[] pestanas = {
                tabInformacion,
                tabSalud,
                tabTurnos,
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

                // Buscar la evaluación más reciente.
                // Las fechas de MySQL tienen formato YYYY-MM-DD HH:mm:ss.
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

                runOnUiThread(() -> {

                    if (resultado == null) {
                        mostrarSaludSinDatos();
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

                    String estado = resultado.optString(
                            "estado_aptitud", ""
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

        // Ordenar desde la más reciente hasta la más antigua.
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

        // Crear una tarjeta por cada evaluación anterior.
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

            // La primera evaluación anterior siempre se muestra.
            // Las demás quedan ocultas hasta presionar el botón.
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
                            + (plaquetas.equals("Sin datos") ? "" : " ×10⁹/L")
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
