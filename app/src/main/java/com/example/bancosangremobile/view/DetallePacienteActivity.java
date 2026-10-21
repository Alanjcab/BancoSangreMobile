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

import com.example.bancosangremobile.R;

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
