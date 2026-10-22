package com.example.bancosangremobile.view;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bancosangremobile.R;
import com.example.bancosangremobile.repository.DonacionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NuevaDonacionActivity extends AppCompatActivity {
    private AutoCompleteTextView edtDonante;
    private EditText edtFecha, edtObservaciones;
    private Spinner spinnerTipo;
    private Button btnGuardar;
    private final List<Integer> idsDonantes = new ArrayList<>();
    private final List<String> nombresDonantes = new ArrayList<>();
    private int idDonanteElegido = 0;
    private String fechaSql;
    private final String[] tipos = {"Sangre entera", "Plasma", "Plaquetas", "Plasma y plaquetas"};
    private final int[] idsTipos = {1, 2, 3, 4};

    @Override protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_nueva_donacion);
        edtDonante = findViewById(R.id.edtDonanteDonacion);
        edtFecha = findViewById(R.id.edtFechaDonacion);
        edtObservaciones = findViewById(R.id.edtObservacionesDonacion);
        spinnerTipo = findViewById(R.id.spinnerTipoDonacion);
        btnGuardar = findViewById(R.id.btnGuardarDonacion);
        findViewById(R.id.btnVolverNuevaDonacion).setOnClickListener(v -> finish());
        spinnerTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos));
        Calendar hoy = Calendar.getInstance();
        establecerFecha(hoy);
        edtFecha.setOnClickListener(v -> {
            Calendar calendario = Calendar.getInstance();
            DatePickerDialog dialogo = new DatePickerDialog(this, (vista, anio, mes, dia) -> {
                Calendar seleccion = Calendar.getInstance();
                seleccion.set(anio, mes, dia);
                establecerFecha(seleccion);
            }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));
            dialogo.getDatePicker().setMaxDate(System.currentTimeMillis());
            dialogo.show();
        });
        edtDonante.setThreshold(1);
        edtDonante.setOnItemClickListener((padre, vista, posicion, id) -> {
            String elegido = (String) padre.getItemAtPosition(posicion);
            int indice = nombresDonantes.indexOf(elegido);
            idDonanteElegido = indice >= 0 ? idsDonantes.get(indice) : 0;
        });
        edtDonante.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                int indice = nombresDonantes.indexOf(s.toString());
                idDonanteElegido = indice >= 0 ? idsDonantes.get(indice) : 0;
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });
        btnGuardar.setOnClickListener(v -> guardar());
        cargarDonantes();
    }

    private void establecerFecha(Calendar fecha) {
        fechaSql = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(fecha.getTime());
        edtFecha.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(fecha.getTime()));
    }

    private void cargarDonantes() {
        edtDonante.setEnabled(false);
        edtDonante.setHint("Cargando donantes...");
        new Thread(() -> {
            try {
                JSONArray lista = new DonacionRepository().listarDonantes();
                List<Integer> ids = new ArrayList<>();
                List<String> nombres = new ArrayList<>();
                for (int i = 0; i < lista.length(); i++) {
                    JSONObject d = lista.optJSONObject(i);
                    if (d == null) continue;
                    ids.add(d.optInt("id_donante"));
                    nombres.add((d.optString("apellido") + ", " + d.optString("nombre")
                            + " - DNI " + d.optString("dni")).trim());
                }
                runOnUiThread(() -> {
                    idsDonantes.clear(); idsDonantes.addAll(ids);
                    nombresDonantes.clear(); nombresDonantes.addAll(nombres);
                    edtDonante.setAdapter(new ArrayAdapter<>(this,
                            android.R.layout.simple_dropdown_item_1line, nombresDonantes));
                    edtDonante.setEnabled(true);
                    edtDonante.setHint("Buscar por nombre o DNI");
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Error al cargar donantes: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void guardar() {
        if (idDonanteElegido <= 0) {
            edtDonante.setError("Seleccioná un donante de la lista");
            return;
        }
        int posicion = spinnerTipo.getSelectedItemPosition();
        if (posicion < 0) return;
        btnGuardar.setEnabled(false);
        try {
            JSONObject datos = new JSONObject();
            datos.put("id_donante", idDonanteElegido);
            datos.put("id_tipo_donacion", idsTipos[posicion]);
            datos.put("fecha_donacion", fechaSql);
            datos.put("observaciones", edtObservaciones.getText().toString().trim());
            new Thread(() -> {
                try {
                    new DonacionRepository().crearDonacion(datos);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Donación registrada correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        btnGuardar.setEnabled(true);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            }).start();
        } catch (Exception e) {
            btnGuardar.setEnabled(true);
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
