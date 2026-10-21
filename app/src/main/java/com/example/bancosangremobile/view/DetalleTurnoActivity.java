package com.example.bancosangremobile.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;

public class DetalleTurnoActivity extends AppCompatActivity {

    private TextView txtEstadoTurno;
    private TextView txtNombreDonante;
    private TextView txtDniDonante;
    private TextView txtFechaDetalle;
    private TextView txtHoraDetalle;
    private TextView txtTipoDetalle;
    private TextView txtObservacionesDetalle;

    private TextView btnEditarTurno;
    private TextView btnCancelarTurno;

    private String estadoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_turno);

        txtEstadoTurno = findViewById(R.id.txtEstadoTurno);
        txtNombreDonante = findViewById(R.id.txtNombreDonante);
        txtDniDonante = findViewById(R.id.txtDniDonante);
        txtFechaDetalle = findViewById(R.id.txtFechaDetalle);
        txtHoraDetalle = findViewById(R.id.txtHoraDetalle);
        txtTipoDetalle = findViewById(R.id.txtTipoDetalle);
        txtObservacionesDetalle =
                findViewById(R.id.txtObservacionesDetalle);

        btnEditarTurno = findViewById(R.id.btnEditarTurno);
        btnCancelarTurno = findViewById(R.id.btnCancelarTurno);

        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        cargarDatosTurno();
        configurarBotones();
    }

    private void cargarDatosTurno() {

        String nombre = getIntent().getStringExtra("nombre");
        String dni = getIntent().getStringExtra("dni");
        String fecha = getIntent().getStringExtra("fecha");
        String hora = getIntent().getStringExtra("hora");
        String tipo = getIntent().getStringExtra("tipo");
        String observaciones =
                getIntent().getStringExtra("observaciones");

        estadoActual = getIntent().getStringExtra("estado");

        txtNombreDonante.setText(valorOTexto(nombre, "Sin nombre"));
        txtDniDonante.setText(
                "DNI: " + valorOTexto(dni, "-")
        );

        txtFechaDetalle.setText(
                "Fecha: " + valorOTexto(fecha, "-")
        );

        txtHoraDetalle.setText(
                "Hora: " + valorOTexto(hora, "-")
        );

        txtTipoDetalle.setText(
                "Tipo de donación: " + valorOTexto(tipo, "-")
        );

        txtObservacionesDetalle.setText(
                valorOTexto(observaciones, "Sin observaciones")
        );

        actualizarEstadoVisual();
    }

    private String valorOTexto(String valor, String alternativa) {
        if (valor == null || valor.trim().isEmpty()) {
            return alternativa;
        }
        return valor;
    }

    private void actualizarEstadoVisual() {

        if (estadoActual == null) {
            estadoActual = "Próximo";
        }

        txtEstadoTurno.setText(estadoActual);

        switch (estadoActual) {
            case "Realizado":
                txtEstadoTurno.setTextColor(0xFF218B55);
                break;

            case "Cancelado":
                txtEstadoTurno.setTextColor(0xFFC62828);
                break;

            default:
                txtEstadoTurno.setTextColor(0xFF0875A5);
                break;
        }

        boolean editable = estadoActual.equals("Próximo");

        btnEditarTurno.setVisibility(
                editable ? View.VISIBLE : View.GONE
        );

        btnCancelarTurno.setVisibility(
                editable ? View.VISIBLE : View.GONE
        );
    }

    private void configurarBotones() {

        btnEditarTurno.setOnClickListener(v -> {
            Toast.makeText(
                    this,
                    "La edición se implementará al conectar los turnos.",
                    Toast.LENGTH_LONG
            ).show();
        });

        btnCancelarTurno.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancelar turno")
                    .setMessage(
                            "¿Estás seguro de que querés cancelar este turno?"
                    )
                    .setNegativeButton("Volver", null)
                    .setPositiveButton("Cancelar turno",
                            (dialog, which) -> {
                                Toast.makeText(
                                        this,
                                        "La cancelación se conectará a la API PHP.",
                                        Toast.LENGTH_LONG
                                ).show();
                            })
                    .show();
        });
    }
}
