package com.example.bancosangremobile.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;

import java.util.Calendar;
import java.util.Locale;

public class FichaTecnicaActivity extends AppCompatActivity {

    private TextView txtFechaEvaluacion;
    private Spinner spinnerAptitud;

    private EditText edtHematocrito;
    private EditText edtPlaquetas;
    private EditText edtFenotipo;
    private EditText edtPeso;
    private EditText edtObservaciones;

    private boolean fechaSeleccionada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_tecnica);

        txtFechaEvaluacion = findViewById(R.id.txtFechaEvaluacion);
        spinnerAptitud = findViewById(R.id.spinnerAptitud);

        edtHematocrito = findViewById(R.id.edtHematocrito);
        edtPlaquetas = findViewById(R.id.edtPlaquetas);
        edtFenotipo = findViewById(R.id.edtFenotipo);
        edtPeso = findViewById(R.id.edtPeso);
        edtObservaciones = findViewById(R.id.edtObservaciones);

        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        String nombre = getIntent().getStringExtra("nombre");
        String dni = getIntent().getStringExtra("dni");

        TextView txtDonanteEvaluado =
                findViewById(R.id.txtDonanteEvaluado);

        if (nombre != null && dni != null) {
            txtDonanteEvaluado.setText(
                    nombre + " - DNI " + dni
            );
        } else {
            txtDonanteEvaluado.setText(
                    "Donante no identificado"
            );
        }

        configurarAptitud();

        txtFechaEvaluacion.setOnClickListener(
                v -> mostrarCalendario()
        );

        findViewById(R.id.btnGuardarFicha)
                .setOnClickListener(v -> validarFormulario());
    }

    private void configurarAptitud() {

        String[] estados = {
                "Seleccionar resultado",
                "Apto",
                "No apto",
                "Con restricción",
                "En evaluación"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                estados
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerAptitud.setAdapter(adapter);
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

                    txtFechaEvaluacion.setText(fecha);
                    fechaSeleccionada = true;
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        );

        dialog.getDatePicker().setMaxDate(
                System.currentTimeMillis()
        );

        dialog.show();
    }

    private void validarFormulario() {

        if (!fechaSeleccionada) {
            mostrarMensaje("Seleccioná una fecha de evaluación");
            return;
        }

        if (!validarNumeroOpcional(edtHematocrito, 0, 100)) {
            mostrarMensaje("Ingresá un hematocrito válido");
            return;
        }

        if (!validarNumeroOpcional(edtPlaquetas, 0, 2000)) {
            mostrarMensaje("Ingresá un valor de plaquetas válido");
            return;
        }

        if (!validarNumeroOpcional(edtPeso, 0, 500)) {
            mostrarMensaje("Ingresá un peso válido");
            return;
        }

        if (spinnerAptitud.getSelectedItemPosition() == 0) {
            mostrarMensaje("Seleccioná el resultado de la evaluación");
            return;
        }

        // Los datos se guardarán cuando conectemos la API PHP.
        mostrarMensaje(
                "Formulario válido. Guardado pendiente de conectar."
        );
    }

    private boolean validarNumeroOpcional(
            EditText campo,
            double minimo,
            double maximo
    ) {

        String texto = campo.getText()
                .toString()
                .trim()
                .replace(",", ".");

        if (texto.isEmpty()) {
            return true;
        }

        try {
            double numero = Double.parseDouble(texto);

            return numero > minimo && numero <= maximo;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }
}
