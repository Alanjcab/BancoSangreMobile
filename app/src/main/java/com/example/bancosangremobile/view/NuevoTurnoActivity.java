package com.example.bancosangremobile.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bancosangremobile.model.Donante;
import com.example.bancosangremobile.repository.DonanteRepository;
import com.example.bancosangremobile.repository.TurnoRepository;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bancosangremobile.R;
import java.util.Calendar;
import java.util.Locale;

public class NuevoTurnoActivity extends AppCompatActivity {

    private EditText edtBuscarDni;
    private TextView btnBuscarDonante;
    private LinearLayout contenedorDonanteEncontrado;
    private TextView txtNombreDonanteEncontrado;
    private TextView txtDniDonanteEncontrado;
    private Donante donanteSeleccionado = null;
    private Spinner spinnerModalidad;
    private RadioGroup grupoTipoDonacion;
    private LinearLayout contenedorModalidad;
    private TextView txtFechaTurno;
    private TextView txtHoraTurno;
    private EditText edtObservaciones;

    private boolean fechaSeleccionada = false;
    private boolean horaSeleccionada = false;
    private String fechaTurnoSql = "";
    private String horaTurnoSql = "";
    private boolean guardandoTurno = false;
    private final List<Donante> listaDonantes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_turno);

        edtBuscarDni = findViewById(R.id.edtBuscarDni);
        btnBuscarDonante = findViewById(R.id.btnBuscarDonante);

        contenedorDonanteEncontrado =
                findViewById(R.id.contenedorDonanteEncontrado);

        txtNombreDonanteEncontrado =
                findViewById(R.id.txtNombreDonanteEncontrado);

        txtDniDonanteEncontrado =
                findViewById(R.id.txtDniDonanteEncontrado);

        btnBuscarDonante.setOnClickListener(v -> buscarDonante());
        spinnerModalidad = findViewById(R.id.spinnerModalidad);
        grupoTipoDonacion = findViewById(R.id.grupoTipoDonacion);
        contenedorModalidad = findViewById(R.id.contenedorModalidad);
        txtFechaTurno = findViewById(R.id.txtFechaTurno);
        txtHoraTurno = findViewById(R.id.txtHoraTurno);
        edtObservaciones = findViewById(R.id.edtObservaciones);

        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnGuardarTurno)
                .setOnClickListener(v -> validarTurno());

        configurarDonantes();
        configurarModalidades();
        configurarTipoDonacion();

        txtFechaTurno.setOnClickListener(
                v -> mostrarCalendario()
        );

        txtHoraTurno.setOnClickListener(
                v -> mostrarReloj()
        );

        edtBuscarDni.addTextChangedListener(new android.text.TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                donanteSeleccionado = null;
                contenedorDonanteEncontrado.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });
    }

    private void configurarDonantes() {

        new Thread(() -> {

            try {
                DonanteRepository repository = new DonanteRepository();

                List<Donante> donantes = repository.listarDonantes();

                runOnUiThread(() -> {
                    listaDonantes.clear();
                    listaDonantes.addAll(donantes);
                });

            } catch (Exception e) {

                runOnUiThread(() ->
                        mostrarMensaje(
                                "Error al cargar donantes: " + e.getMessage()
                        )
                );
            }

        }).start();
    }

    private void buscarDonante() {

        String dniBuscado = edtBuscarDni.getText().toString().trim();

        donanteSeleccionado = null;
        contenedorDonanteEncontrado.setVisibility(View.GONE);

        if (dniBuscado.isEmpty()) {
            mostrarMensaje("Ingresá el DNI del donante");
            return;
        }

        for (Donante donante : listaDonantes) {

            if (donante.getDni().equals(dniBuscado)) {

                donanteSeleccionado = donante;

                txtNombreDonanteEncontrado.setText(
                        donante.getNombre()
                );

                txtDniDonanteEncontrado.setText(
                        "DNI: " + donante.getDni()
                );

                contenedorDonanteEncontrado.setVisibility(View.VISIBLE);
                return;
            }
        }

        mostrarMensaje("No se encontró un donante con ese DNI");
    }

    private void configurarModalidades() {

        String[] modalidades = {
                "Tipo de donacion",
                "Plasma",
                "Plaquetas",
                "Plasma/Plaquetas"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                modalidades
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerModalidad.setAdapter(adapter);
    }

    private void configurarTipoDonacion() {

        grupoTipoDonacion.setOnCheckedChangeListener(
                (group, checkedId) -> {

                    if (checkedId == R.id.radioAferesis) {
                        contenedorModalidad.setVisibility(View.VISIBLE);
                    } else {
                        contenedorModalidad.setVisibility(View.GONE);
                        spinnerModalidad.setSelection(0);
                    }
                }
        );
    }

    private void mostrarCalendario() {

        Calendar calendario = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
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

                    fechaTurnoSql = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            anio,
                            mes + 1,
                            dia
                    );

                    fechaSeleccionada = true;
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        // No permitir turnos en fechas anteriores
        dialog.getDatePicker().setMinDate(
                System.currentTimeMillis() - 1000
        );

        dialog.show();
    }

    private void mostrarReloj() {

        Calendar calendario = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hora, minuto) -> {

                    String horario = String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            hora,
                            minuto
                    );

                    txtHoraTurno.setText(horario);

                    horaTurnoSql = String.format(
                            Locale.US,
                            "%02d:%02d:00",
                            hora,
                            minuto
                    );

                    horaSeleccionada = true;
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void validarTurno() {

        if (donanteSeleccionado == null) {
            mostrarMensaje("Buscá y seleccioná un donante válido");
            return;
        }

        int tipoSeleccionado =
                grupoTipoDonacion.getCheckedRadioButtonId();

        if (tipoSeleccionado == -1) {
            mostrarMensaje("Seleccioná el tipo de donación");
            return;
        }

        if (tipoSeleccionado == R.id.radioAferesis
                && spinnerModalidad.getSelectedItemPosition() == 0) {

            mostrarMensaje("Seleccioná la modalidad de aféresis");
            return;
        }

        if (!fechaSeleccionada) {
            mostrarMensaje("Seleccioná una fecha");
            return;
        }

        if (!horaSeleccionada) {
            mostrarMensaje("Seleccioná una hora");
            return;
        }

        int idTipoDonacion;

        if (tipoSeleccionado == R.id.radioSangre) {

            idTipoDonacion = 1;

        } else {

            // Posición 1 = Plasma (ID 2)
            // Posición 2 = Plaquetas (ID 3)
            // Posición 3 = Plasma/Plaquetas (ID 4)
            idTipoDonacion = spinnerModalidad.getSelectedItemPosition() + 1;
        }

        guardarTurno(
                donanteSeleccionado.getIdDonante(),
                idTipoDonacion
        );
    }

    private void guardarTurno(int idDonante, int idTipoDonacion) {

        if (guardandoTurno) {
            return;
        }

        guardandoTurno = true;
        findViewById(R.id.btnGuardarTurno).setEnabled(false);

        String fecha = fechaTurnoSql;
        String hora = horaTurnoSql;
        String observaciones =
                edtObservaciones.getText().toString().trim();

        new Thread(() -> {

            try {

                JSONObject datos = new JSONObject();

                datos.put("id_donante", idDonante);
                datos.put("id_tipo_donacion", idTipoDonacion);
                datos.put("fecha_turno", fecha);
                datos.put("hora_turno", hora);

                if (observaciones.isEmpty()) {
                    datos.put("observaciones", JSONObject.NULL);
                } else {
                    datos.put("observaciones", observaciones);
                }

                TurnoRepository repository = new TurnoRepository();

                int idTurno = repository.crearTurno(datos);

                runOnUiThread(() -> {

                    mostrarMensaje(
                            "Turno registrado correctamente. ID: " + idTurno
                    );

                    setResult(RESULT_OK);
                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    guardandoTurno = false;
                    findViewById(R.id.btnGuardarTurno).setEnabled(true);

                    mostrarMensaje(
                            "Error al guardar el turno: " + e.getMessage()
                    );
                });
            }

        }).start();
    }
    private void mostrarMensaje(String mensaje) {
        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
