package com.example.bancosangremobile.view;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.repository.DonacionRepository;

import org.json.JSONObject;

public class DetalleDonacionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_detalle_donacion);
        findViewById(R.id.btnVolverDetalleDonacion).setOnClickListener(v -> finish());
        int id = getIntent().getIntExtra("id_donacion", 0);
        if (id <= 0) {
            Toast.makeText(this, "Donación inválida", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        new Thread(() -> {
            try {
                JSONObject d = new DonacionRepository().obtenerDonacion(id);
                runOnUiThread(() -> {
                    escribir(R.id.txtDetalleNombre, d.optString("nombre") + " " + d.optString("apellido"));
                    escribir(R.id.txtDetalleDni, "DNI: " + d.optString("dni"));
                    escribir(R.id.txtDetalleFecha, "Fecha: " + DonacionesActivity.fechaVisual(d.optString("fecha_donacion")));
                    escribir(R.id.txtDetalleTipo, "Modalidad: " + d.optString("tipo_donacion"));
                    String observaciones = d.isNull("observaciones") ? "" : d.optString("observaciones", "").trim();
                    escribir(R.id.txtDetalleObservaciones, "Observaciones: " + (observaciones.isEmpty() ? "Sin observaciones" : observaciones));
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void escribir(int id, String valor) {
        ((TextView) findViewById(id)).setText(valor);
    }
}
