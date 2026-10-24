package com.example.bancosangremobile.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.repository.DonacionRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class DonacionesActivity extends AppCompatActivity {
    private LinearLayout contenedor;
    private EditText buscar;
    private JSONArray donaciones = new JSONArray();

    @Override
    protected void onCreate(Bundle estado) {
        super.onCreate(estado);
        setContentView(R.layout.activity_donaciones);
        contenedor = findViewById(R.id.contenedorDonaciones);
        buscar = findViewById(R.id.edtBuscarDonacion);
        findViewById(R.id.btnVolverDonaciones).setOnClickListener(v -> finish());
        findViewById(R.id.btnNuevaDonacion).setOnClickListener(v ->
                startActivity(new Intent(this, NuevaDonacionActivity.class)));
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mostrar();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private void cargar() {
        contenedor.removeAllViews();
        contenedor.addView(texto("Cargando donaciones...", 14, false));
        new Thread(() -> {
            try {
                JSONArray lista = new DonacionRepository().listarDonaciones();
                runOnUiThread(() -> {
                    donaciones = lista;
                    mostrar();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    contenedor.removeAllViews();
                    contenedor.addView(texto("No se pudieron cargar las donaciones: " + e.getMessage(), 14, false));
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void mostrar() {
        contenedor.removeAllViews();
        String consulta = buscar.getText().toString().trim().toLowerCase(Locale.ROOT);
        int encontrados = 0;
        for (int i = 0; i < donaciones.length(); i++) {
            JSONObject d = donaciones.optJSONObject(i);
            if (d == null) continue;
            String nombre = (d.optString("nombre") + " " + d.optString("apellido")).trim();
            String dni = d.optString("dni", "");
            if (!(nombre + " " + dni).toLowerCase(Locale.ROOT).contains(consulta)) continue;
            encontrados++;
            LinearLayout tarjeta = new LinearLayout(this);
            tarjeta.setOrientation(LinearLayout.VERTICAL);
            tarjeta.setPadding(dp(16), dp(14), dp(16), dp(14));
            GradientDrawable fondo = new GradientDrawable();
            fondo.setColor(Color.WHITE);
            fondo.setCornerRadius(dp(16));
            fondo.setStroke(dp(1), Color.parseColor("#DDE8ED"));
            tarjeta.setBackground(fondo);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(12);
            tarjeta.setLayoutParams(lp);
            tarjeta.addView(texto(nombre, 17, true));
            tarjeta.addView(texto("DNI " + dni, 13, false));
            tarjeta.addView(texto("Fecha: " + fechaVisual(d.optString("fecha_donacion")), 14, false));
            tarjeta.addView(texto("Modalidad: " + d.optString("tipo_donacion"), 14, false));
            int id = d.optInt("id_donacion");
            tarjeta.setOnClickListener(v -> {
                Intent intent = new Intent(this, DetalleDonacionActivity.class);
                intent.putExtra("id_donacion", id);
                startActivity(intent);
            });
            contenedor.addView(tarjeta);
        }
        if (encontrados == 0) contenedor.addView(texto("No se encontraron donaciones.", 14, false));
    }

    private TextView texto(String contenido, int tam, boolean negrita) {
        TextView tv = new TextView(this);
        tv.setText(contenido);
        tv.setTextSize(tam);
        tv.setTextColor(Color.parseColor(negrita ? "#163B4A" : "#597487"));
        if (negrita) tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.START);
        tv.setPadding(0, dp(3), 0, dp(3));
        return tv;
    }

    private int dp(int valor) {
        return Math.round(valor * getResources().getDisplayMetrics().density);
    }

    public static String fechaVisual(String fecha) {
        if (fecha != null && fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return fecha.substring(8) + "/" + fecha.substring(5, 7) + "/" + fecha.substring(0, 4);
        }
        return fecha == null ? "" : fecha;
    }
}
