package com.example.bancosangremobile.view;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;

import java.util.Locale;

import com.example.bancosangremobile.repository.EvaluacionRepository;

import org.json.JSONObject;

public class FichaTecnicaActivity extends AppCompatActivity {

    private TextView txtFechaEvaluacion;
    private Spinner spinnerAptitud;
    private EditText edtHematocrito;
    private EditText edtPlaquetas;
    private EditText edtFenotipo;
    private EditText edtPeso;
    private EditText edtObservaciones;
    private boolean fechaSeleccionada = false;
    private EvaluacionRepository evaluacionRepository;
    private int idDonante;
    private boolean guardando = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficha_tecnica);

        evaluacionRepository = new EvaluacionRepository();

        idDonante = getIntent().getIntExtra("id_donante", 0);

        if (idDonante <= 0) {
            Toast.makeText(
                    this,
                    "No se pudo identificar al donante",
                    Toast.LENGTH_LONG
            ).show();
            finish();
            return;
        }

        txtFechaEvaluacion = findViewById(R.id.txtFechaEvaluacion);
        java.text.SimpleDateFormat formato =
                new java.text.SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        txtFechaEvaluacion.setText(
                formato.format(new java.util.Date())
        );
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

        findViewById(R.id.btnGuardarFicha)
                .setOnClickListener(v -> validarFormulario());
    }

    private void configurarAptitud() {

        String[] estados = {
                "Seleccionar resultado",
                "Habilitado",
                "Advertencia",
                "No habilitado"
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

    private void validarFormulario() {

        if (!validarNumeroOpcional(edtHematocrito, 0, 100)) {
            mostrarMensaje("Ingresá un hematocrito válido");
            return;
        }

        if (!validarNumeroOpcional(edtPlaquetas, -1, 2000)) {
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

        guardarEvaluacion();
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


    private void guardarEvaluacion() {

        if (guardando) {
            return;
        }

        guardando = true;
        findViewById(R.id.btnGuardarFicha).setEnabled(false);

        try {
            JSONObject datos = new JSONObject();

            datos.put("id_donante", idDonante);

            agregarNumero(datos, "peso_kg", edtPeso);
            agregarNumero(datos, "hematocrito", edtHematocrito);
            agregarNumero(datos, "recuento_plaquetario", edtPlaquetas);

            String[] estados = {
                    "",
                    "HABILITADO",
                    "ADVERTENCIA",
                    "NO_HABILITADO"
            };

            datos.put(
                    "estado_aptitud",
                    estados[spinnerAptitud.getSelectedItemPosition()]
            );

            datos.put(
                    "observaciones",
                    edtObservaciones.getText().toString().trim()
            );

            new Thread(() -> {
                try {
                    int idEvaluacion =
                            evaluacionRepository.crearEvaluacion(datos);

                    runOnUiThread(() -> {
                        mostrarMensaje(
                                "Ficha técnica guardada correctamente"
                        );
                        finish();
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        guardando = false;
                        findViewById(R.id.btnGuardarFicha)
                                .setEnabled(true);

                        mostrarMensaje(e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            guardando = false;
            findViewById(R.id.btnGuardarFicha).setEnabled(true);
            mostrarMensaje("Error al preparar la ficha técnica");
        }
    }

    private void agregarNumero(
            JSONObject datos,
            String nombre,
            EditText campo
    ) throws Exception {

        String valor = campo.getText()
                .toString()
                .trim()
                .replace(",", ".");

        if (valor.isEmpty()) {
            datos.put(nombre, JSONObject.NULL);
        } else if (nombre.equals("recuento_plaquetario")) {
            datos.put(nombre, Integer.parseInt(valor));
        } else {
            datos.put(nombre, Double.parseDouble(valor));
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
