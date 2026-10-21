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

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;

import java.util.Calendar;
import java.util.Locale;

public class NuevoTurnoActivity extends AppCompatActivity {

    private Spinner spinnerDonante;
    private Spinner spinnerModalidad;
    private RadioGroup grupoTipoDonacion;
    private LinearLayout contenedorModalidad;
    private TextView txtFechaTurno;
    private TextView txtHoraTurno;
    private EditText edtObservaciones;

    private boolean fechaSeleccionada = false;
    private boolean horaSeleccionada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_turno);

        spinnerDonante = findViewById(R.id.spinnerDonante);
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
    }

    private void configurarDonantes() {

        // Datos de ejemplo. Luego vendrán de nuestra API PHP.
        String[] donantes = {
                "Seleccionar donante",
                "Juan Pérez - DNI 28123456",
                "María González - DNI 32456789",
                "Carlos Ramírez - DNI 30567123",
                "Lucía Fernández - DNI 27890234"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                donantes
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerDonante.setAdapter(adapter);
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
                    horaSeleccionada = true;
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
        );

        dialog.show();
    }

    private void validarTurno() {

        if (spinnerDonante.getSelectedItemPosition() == 0) {
            mostrarMensaje("Seleccioná un donante");
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

        // Por ahora no guardamos datos en MySQL.
        mostrarMensaje(
                "Formulario válido. El guardado se conectará a la API PHP."
        );
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
