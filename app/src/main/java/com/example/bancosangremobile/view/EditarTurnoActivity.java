package com.example.bancosangremobile.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.repository.TurnoRepository;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class EditarTurnoActivity extends AppCompatActivity {

    private int idDonanteActual;
    private String estadoActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_turno);
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // REFERENCIAS A LOS COMPONENTES

        TextView txtFechaTurno = findViewById(R.id.txtFechaTurno);
        TextView txtHoraTurno = findViewById(R.id.txtHoraTurno);

        EditText edtObservaciones = findViewById(R.id.edtObservaciones);
        EditText edtBuscarDni = findViewById(R.id.edtBuscarDni);

        TextView txtNombreDonanteEncontrado =
                findViewById(R.id.txtNombreDonanteEncontrado);

        TextView txtDniDonanteEncontrado =
                findViewById(R.id.txtDniDonanteEncontrado);

        LinearLayout contenedorDonanteEncontrado =
                findViewById(R.id.contenedorDonanteEncontrado);

        RadioButton radioSangre = findViewById(R.id.radioSangre);
        RadioButton radioAferesis = findViewById(R.id.radioAferesis);

        Spinner spinnerModalidad = findViewById(R.id.spinnerModalidad);

        String[] modalidades = {
                "Tipo de donacion",
                "Plasma",
                "Plaquetas",
                "Plasma/Plaquetas"
        };

        android.widget.ArrayAdapter<String> adapter =
                new android.widget.ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        modalidades
                );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerModalidad.setAdapter(adapter);

        LinearLayout contenedorModalidad =
                findViewById(R.id.contenedorModalidad);

        // OCULTAR BÚSQUEDA DE DONANTE

        findViewById(R.id.btnBuscarDonante).setVisibility(View.GONE);

        edtBuscarDni.setEnabled(false);

        // SELECCIONAR FECHA

        txtFechaTurno.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            String fechaActual = txtFechaTurno.getText().toString().trim();

            if (fechaActual.matches("\\d{2}/\\d{2}/\\d{4}")) {
                String[] partes = fechaActual.split("/");

                calendario.set(
                        Integer.parseInt(partes[2]),
                        Integer.parseInt(partes[1]) - 1,
                        Integer.parseInt(partes[0])
                );
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, anio, mes, dia) -> {

                        String fecha = String.format(
                                Locale.getDefault(),
                                "%02d/%02d/%04d",
                                dia,
                                mes + 1,
                                anio
                        );

                        txtFechaTurno.setText(fecha);
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
        });

        // SELECCIONAR HORA

        txtHoraTurno.setOnClickListener(v -> {

            Calendar calendario = Calendar.getInstance();

            int hora = calendario.get(Calendar.HOUR_OF_DAY);
            int minuto = calendario.get(Calendar.MINUTE);

            String horaActual = txtHoraTurno.getText().toString().trim();

            if (horaActual.matches("\\d{2}:\\d{2}")) {
                String[] partes = horaActual.split(":");

                hora = Integer.parseInt(partes[0]);
                minuto = Integer.parseInt(partes[1]);
            }

            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, horaSeleccionada, minutoSeleccionado) -> {

                        String nuevaHora = String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                horaSeleccionada,
                                minutoSeleccionado
                        );

                        txtHoraTurno.setText(nuevaHora);
                    },
                    hora,
                    minuto,
                    true
            );

            timePickerDialog.show();
        });

        // TIPO DE DONACIÓN

        radioSangre.setOnClickListener(v -> {
            contenedorModalidad.setVisibility(View.GONE);
        });

        radioAferesis.setOnClickListener(v -> {
            contenedorModalidad.setVisibility(View.VISIBLE);
        });

        // OBTENER ID DEL TURNO

        int idTurno = getIntent().getIntExtra("id_turno", 0);

        if (idTurno <= 0) {
            Toast.makeText(
                    this,
                    "No se encontró el turno",
                    Toast.LENGTH_LONG
            ).show();

            finish();
            return;
        }

        // CARGAR DATOS DEL TURNO DESDE MYSQL

        new Thread(() -> {

            try {

                TurnoRepository repository = new TurnoRepository();

                JSONObject turno = repository.obtenerTurnoPorId(idTurno);

                runOnUiThread(() -> {

                    // FECHA
                    idDonanteActual = turno.optInt("id_donante", 0);
                    estadoActual = turno.optString("estado", "");

                    String fecha = turno.optString("fecha_turno", "");

                    if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {

                        String[] partes = fecha.split("-");

                        fecha = partes[2] + "/" + partes[1] + "/" + partes[0];
                    }

                    txtFechaTurno.setText(fecha);

                    // HORA

                    String hora = turno.optString("hora_turno", "");

                    if (hora.length() >= 5) {
                        hora = hora.substring(0, 5);
                    }

                    txtHoraTurno.setText(hora);

                    // OBSERVACIONES

                    String observaciones =
                            turno.optString("observaciones", "");

                    if (turno.isNull("observaciones")) {
                        observaciones = "";
                    }

                    edtObservaciones.setText(observaciones);

                    // DONANTE

                    String dni = turno.optString("dni", "");

                    edtBuscarDni.setText(dni);

                    String nombreCompleto = (
                            turno.optString("nombre", "")
                                    + " "
                                    + turno.optString("apellido", "")
                    ).trim();

                    txtNombreDonanteEncontrado.setText(nombreCompleto);

                    txtDniDonanteEncontrado.setText(dni);

                    contenedorDonanteEncontrado.setVisibility(View.VISIBLE);

                    // TIPO DE DONACIÓN

                    int idTipoDonacion =
                            turno.optInt("id_tipo_donacion", 0);

                    if (idTipoDonacion == 1) {

                        radioSangre.setChecked(true);
                        contenedorModalidad.setVisibility(View.GONE);

                    } else if (idTipoDonacion >= 2 && idTipoDonacion <= 4) {

                        radioAferesis.setChecked(true);

                        contenedorModalidad.setVisibility(View.VISIBLE);

                        spinnerModalidad.setSelection(idTipoDonacion - 1);
                    }

                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    Toast.makeText(
                            this,
                            "Error al cargar el turno: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
            }

        }).start();

        findViewById(R.id.btnGuardarTurno).setOnClickListener(v -> {

            String fecha = txtFechaTurno.getText().toString().trim();
            String hora = txtHoraTurno.getText().toString().trim();
            String observaciones = edtObservaciones.getText().toString().trim();

            if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")
                    || !hora.matches("\\d{2}:\\d{2}")) {

                Toast.makeText(this,
                        "Seleccioná una fecha y hora válidas",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (idDonanteActual <= 0 || estadoActual == null || estadoActual.isEmpty()) {
                Toast.makeText(this,
                        "Todavía no se cargaron los datos del turno",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int idTipoDonacion;

            if (radioSangre.isChecked()) {
                idTipoDonacion = 1;
            } else if (radioAferesis.isChecked()
                    && spinnerModalidad.getSelectedItemPosition() >= 1
                    && spinnerModalidad.getSelectedItemPosition() <= 3) {

                idTipoDonacion = spinnerModalidad.getSelectedItemPosition() + 1;
            } else {
                Toast.makeText(this,
                        "Seleccioná un tipo de donación",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String[] partesFecha = fecha.split("/");
            String fechaMysql = partesFecha[2] + "-"
                    + partesFecha[1] + "-" + partesFecha[0];

            findViewById(R.id.btnGuardarTurno).setEnabled(false);

            new Thread(() -> {
                try {
                    JSONObject datos = new JSONObject();

                    datos.put("id_turno", idTurno);
                    datos.put("id_donante", idDonanteActual);
                    datos.put("id_tipo_donacion", idTipoDonacion);
                    datos.put("fecha_turno", fechaMysql);
                    datos.put("hora_turno", hora);
                    datos.put("estado", estadoActual);
                    datos.put("observaciones", observaciones);

                    TurnoRepository repository = new TurnoRepository();
                    repository.actualizarTurno(datos);

                    runOnUiThread(() -> {
                        Toast.makeText(this,
                                "Turno actualizado correctamente",
                                Toast.LENGTH_LONG).show();

                        setResult(RESULT_OK);
                        finish();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        findViewById(R.id.btnGuardarTurno).setEnabled(true);

                        Toast.makeText(this,
                                "Error al actualizar: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        });

        // AJUSTAR PANTALLA

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
}