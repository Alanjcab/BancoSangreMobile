package com.example.bancosangremobile.view;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class EstadisticasActivity extends AppCompatActivity {
    private LinearLayout contenedor;
    private TextView estado;
    private final String[] periodos = {"Esta semana", "Este mes", "Este año"};
    private final String[] codigos = {"semana", "mes", "anio"};
    private final int AZUL = Color.parseColor("#087D91");
    private final int ROSA = Color.parseColor("#F7065D");
    private final int TEXTO = Color.parseColor("#163B4A");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sesion = getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE);
        String rol = sesion.getString("rol", "").toUpperCase(Locale.ROOT).trim();
        if (!rol.equals("ADMIN") && !rol.equals("SUPERADMIN")) {
            finish();
            return;
        }
        setContentView(R.layout.activity_estadisticas);
        findViewById(R.id.btnVolverEstadisticas).setOnClickListener(v -> finish());
        contenedor = findViewById(R.id.contenedorEstadisticas);
        estado = findViewById(R.id.txtEstadoEstadisticas);
        Spinner selector = findViewById(R.id.spPeriodoEstadisticas);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, periodos);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selector.setAdapter(adaptador);
        selector.setSelection(1);
        selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                cargar(codigos[pos]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });
    }

    private void cargar(String periodo) {
        estado.setText("Cargando estadísticas...");
        contenedor.removeAllViews();
        String token = getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE).getString("token", "");
        new Thread(() -> {
            HttpURLConnection conexion = null;
            try {
                URL url = new URL(ApiConfig.getBaseUrl() + "estadisticas/resumen.php?periodo=" + periodo);
                conexion = (HttpURLConnection) url.openConnection();
                conexion.setRequestMethod("GET");
                conexion.setConnectTimeout(10000);
                conexion.setReadTimeout(10000);
                conexion.setRequestProperty("Accept", "application/json");
                conexion.setRequestProperty("Authorization", "Bearer " + token);
                int codigo = conexion.getResponseCode();
                InputStream flujo = codigo < 400 ? conexion.getInputStream() : conexion.getErrorStream();
                if (flujo == null) throw new Exception("Sin respuesta del servidor");
                StringBuilder respuesta = new StringBuilder();
                try (BufferedReader lector = new BufferedReader(new InputStreamReader(flujo, StandardCharsets.UTF_8))) {
                    String linea;
                    while ((linea = lector.readLine()) != null) respuesta.append(linea);
                }
                JSONObject json = new JSONObject(respuesta.toString());
                if (!json.optBoolean("success"))
                    throw new Exception(json.optString("message", "Error del servidor"));
                JSONObject datos = json.getJSONObject("data");
                runOnUiThread(() -> mostrar(datos));
            } catch (Exception e) {
                runOnUiThread(() -> estado.setText("No se pudieron cargar las estadísticas: " + e.getMessage()));
            } finally {
                if (conexion != null) conexion.disconnect();
            }
        }).start();
    }

    private void mostrar(JSONObject datos) {
        estado.setText("Período: " + datos.optString("desde") + " al " + datos.optString("hasta_exclusivo") + " (sin incluir último día)");
        contenedor.removeAllViews();
        LinearLayout resumen = tarjeta();
        resumen.addView(texto("RESUMEN", 17, true, TEXTO));
        fila(resumen, "Donantes registrados (total)", datos.optInt("total_donantes"));
        fila(resumen, "Nuevos donantes", datos.optInt("nuevos_donantes"));
        fila(resumen, "Donaciones realizadas", datos.optInt("donaciones"));
        fila(resumen, "Donantes que donaron", datos.optInt("donantes_activos"));
        contenedor.addView(resumen);
        JSONArray sexos = datos.optJSONArray("sexos");
        grafico("DONANTES NUEVOS POR SEXO", sexos, ROSA);
        JSONArray modalidades = datos.optJSONArray("modalidades");
        grafico("DONACIONES POR MODALIDAD", modalidades, AZUL);
        LinearLayout evolucion = tarjeta();
        evolucion.addView(texto("EVOLUCIÓN MENSUAL · " + datos.optInt("anio_evolucion"), 17, true, TEXTO));
        JSONArray meses = datos.optJSONArray("evolucion_anual");
        String[] nombres = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        int max = 1;
        if (meses != null)
            for (int i = 0; i < meses.length(); i++) max = Math.max(max, meses.optInt(i));
        for (int i = 0; i < 12; i++)
            barra(evolucion, nombres[i], meses == null ? 0 : meses.optInt(i), max, AZUL);
        contenedor.addView(evolucion);
    }

    private void grafico(String titulo, JSONArray filas, int color) {
        LinearLayout bloque = tarjeta();
        bloque.addView(texto(titulo, 17, true, TEXTO));
        int max = 1;
        if (filas != null) for (int i = 0; i < filas.length(); i++)
            max = Math.max(max, filas.optJSONObject(i).optInt("cantidad"));
        if (filas == null || filas.length() == 0)
            bloque.addView(texto("Sin registros en este período", 14, false, TEXTO));
        else for (int i = 0; i < filas.length(); i++) {
            JSONObject f = filas.optJSONObject(i);
            if (f != null)
                barra(bloque, f.optString("nombre", f.optString("sexo", "Otro")), f.optInt("cantidad"), max, color);
        }
        contenedor.addView(bloque);
    }

    private void barra(LinearLayout padre, String etiqueta, int valor, int max, int color) {
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.VERTICAL);
        fila.setPadding(0, dp(9), 0, dp(4));
        fila.addView(texto(etiqueta + "  ·  " + valor, 14, false, TEXTO));
        LinearLayout pista = new LinearLayout(this);
        pista.setBackground(fondo("#E3F1F5", 8));
        LinearLayout.LayoutParams pistaLp = new LinearLayout.LayoutParams(-1, dp(12));
        pistaLp.topMargin = dp(5);
        LinearLayout avance = new LinearLayout(this);
        avance.setBackground(fondo(color == ROSA ? "#F7065D" : "#0797A8", 8));
        pista.addView(avance, new LinearLayout.LayoutParams(0, -1, Math.max(0.001f, (float) valor / max)));
        // La barra de fondo completa el ancho sin depender del tamaño de pantalla.
        pista.addView(new View(this), new LinearLayout.LayoutParams(0, -1, Math.max(0.001f, 1f - (float) valor / max)));
        fila.addView(pista, pistaLp);
        padre.addView(fila);
    }

    private void fila(LinearLayout padre, String nombre, int valor) {
        TextView tv = texto(nombre + ":  " + valor, 15, false, TEXTO);
        tv.setPadding(0, dp(8), 0, dp(3));
        padre.addView(tv);
    }

    private LinearLayout tarjeta() {
        LinearLayout bloque = new LinearLayout(this);
        bloque.setOrientation(LinearLayout.VERTICAL);
        bloque.setPadding(dp(16), dp(16), dp(16), dp(16));
        bloque.setBackground(fondo("#FFFFFF", 16));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(14);
        bloque.setLayoutParams(lp);
        return bloque;
    }

    private GradientDrawable fondo(String color, int radio) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(Color.parseColor(color));
        d.setCornerRadius(dp(radio));
        return d;
    }

    private TextView texto(String contenido, int tam, boolean negrita, int color) {
        TextView t = new TextView(this);
        t.setText(contenido);
        t.setTextSize(tam);
        t.setTextColor(color);
        if (negrita) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private int dp(int x) {
        return Math.round(x * getResources().getDisplayMetrics().density);
    }
}
