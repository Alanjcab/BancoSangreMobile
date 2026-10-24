package com.example.bancosangremobile.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.repository.TurnoRepository;

import org.json.JSONObject;

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
    private String estadoReal;

    private int idTurno;
    private int idDonante;
    private int idTipoDonacion;

    private final ActivityResultLauncher<Intent> editarTurnoLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            recargarTurnoDesdeApi();
                        }
                    }
            );

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

        idTurno = getIntent().getIntExtra("id_turno", 0);
        idDonante = getIntent().getIntExtra("id_donante", 0);
        idTipoDonacion = getIntent().getIntExtra("id_tipo_donacion", 0);

        String nombre = getIntent().getStringExtra("nombre");
        String dni = getIntent().getStringExtra("dni");
        String fecha = getIntent().getStringExtra("fecha");
        String hora = getIntent().getStringExtra("hora");
        String tipo = getIntent().getStringExtra("tipo");
        String observaciones =
                getIntent().getStringExtra("observaciones");

        estadoActual = getIntent().getStringExtra("estado");
        estadoReal = getIntent().getStringExtra("estado_real");

        txtNombreDonante.setText(valorOTexto(nombre, "Sin nombre"));
        txtDniDonante.setText("DNI: " + valorOTexto(dni, "-"));
        txtFechaDetalle.setText("Fecha: " + valorOTexto(fecha, "-"));
        txtHoraDetalle.setText("Hora: " + valorOTexto(hora, "-"));
        txtTipoDetalle.setText(
                "Tipo de donación: " + valorOTexto(tipo, "-")
        );
        txtObservacionesDetalle.setText(
                valorOTexto(observaciones, "Sin observaciones")
        );

        actualizarEstadoVisual();
    }

    //recargo el truno actualizado de la db

    private void recargarTurnoDesdeApi() {

        new Thread(() -> {

            try {
                TurnoRepository repository = new TurnoRepository();

                JSONObject turno = repository.obtenerTurnoPorId(idTurno);

                runOnUiThread(() -> {

                    idDonante = turno.optInt("id_donante", 0);
                    idTipoDonacion = turno.optInt("id_tipo_donacion", 0);

                    String nombre = (
                            turno.optString("nombre", "") + " " +
                                    turno.optString("apellido", "")
                    ).trim();

                    String dni = turno.optString("dni", "");

                    String fecha = turno.optString("fecha_turno", "");

                    if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        String[] partes = fecha.split("-");
                        fecha = partes[2] + "/" + partes[1] + "/" + partes[0];
                    }

                    String hora = turno.optString("hora_turno", "");

                    if (hora.length() >= 5) {
                        hora = hora.substring(0, 5);
                    }

                    String tipo;

                    switch (idTipoDonacion) {
                        case 1:
                            tipo = "Sangre";
                            break;
                        case 2:
                            tipo = "Plasma";
                            break;
                        case 3:
                            tipo = "Plaquetas";
                            break;
                        case 4:
                            tipo = "Plasma/Plaquetas";
                            break;
                        default:
                            tipo = turno.optString("tipo_donacion", "-");
                            break;
                    }

                    String observaciones =
                            turno.optString("observaciones", "");

                    if (turno.isNull("observaciones")) {
                        observaciones = "";
                    }

                    estadoReal = turno.optString("estado", "PROGRAMADO");

                    switch (estadoReal) {
                        case "REALIZADO":
                            estadoActual = "Realizado";
                            break;

                        case "CANCELADO":
                            estadoActual = "Cancelado";
                            break;

                        default:
                            estadoActual = "Próximo";
                            break;
                    }

                    txtNombreDonante.setText(
                            valorOTexto(nombre, "Sin nombre")
                    );

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
                            valorOTexto(
                                    observaciones,
                                    "Sin observaciones"
                            )
                    );

                    actualizarEstadoVisual();
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        Toast.makeText(
                                this,
                                "Error al actualizar el detalle: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
            }

        }).start();
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

        btnEditarTurno.setOnClickListener(v -> abrirEdicionTurno());

        btnCancelarTurno.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Cancelar turno")
                    .setMessage("¿Estás seguro de que querés cancelar este turno?")
                    .setNegativeButton("Volver", null)
                    .setPositiveButton("Cancelar turno", (dialog, which) -> {

                        btnCancelarTurno.setEnabled(false);

                        new Thread(() -> {
                            try {
                                TurnoRepository repository = new TurnoRepository();

                                repository.cancelarTurno(idTurno);

                                runOnUiThread(() -> {

                                    estadoReal = "CANCELADO";
                                    estadoActual = "Cancelado";

                                    actualizarEstadoVisual();

                                    Toast.makeText(
                                            this,
                                            "Turno cancelado correctamente",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    recargarTurnoDesdeApi();
                                });

                            } catch (Exception e) {

                                runOnUiThread(() -> {

                                    btnCancelarTurno.setEnabled(true);

                                    Toast.makeText(
                                            this,
                                            "Error al cancelar el turno: " + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });
                            }
                        }).start();
                    })
                    .show();
        });
    }

    private void abrirEdicionTurno() {

        Intent intent = new Intent(
                DetalleTurnoActivity.this,
                EditarTurnoActivity.class
        );

        intent.putExtra("id_turno", idTurno);

        editarTurnoLauncher.launch(intent);
    }
}