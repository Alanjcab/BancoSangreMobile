package com.example.bancosangremobile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UsuariosActivity extends AppCompatActivity {
    private LinearLayout contenedor;
    private TextView estado;
    private ProgressBar progreso;
    private SharedPreferences sesion;
    private final int texto = Color.rgb(22, 59, 74), secundario = Color.rgb(82, 112, 128);
    private final int turquesa = Color.rgb(7, 151, 168), fucsia = Color.rgb(247, 6, 93);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);
        contenedor = findViewById(R.id.contenedorUsuarios);
        estado = findViewById(R.id.txtEstadoUsuarios);
        progreso = findViewById(R.id.progresoUsuarios);
        sesion = getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE);
        findViewById(R.id.btnVolverUsuarios).setOnClickListener(v -> finish());
        findViewById(R.id.btnNuevoUsuario).setOnClickListener(v -> startActivity(new Intent(this, FormularioUsuarioActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    private JSONObject solicitar(String archivo, JSONObject datos) throws Exception {
        String token = sesion.getString("token", "");
        if (token.isEmpty()) throw new Exception("Iniciá sesión nuevamente");
        HttpURLConnection c = (HttpURLConnection) new URL(ApiConfig.getUsuariosUrl(archivo)).openConnection();
        try {
            c.setConnectTimeout(10000);
            c.setReadTimeout(10000);
            c.setRequestProperty("Authorization", "Bearer " + token);
            c.setRequestProperty("Accept", "application/json");
            if (datos != null) {
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream os = c.getOutputStream()) {
                    os.write(datos.toString().getBytes(StandardCharsets.UTF_8));
                }
            } else c.setRequestMethod("GET");
            int status = c.getResponseCode();
            InputStream stream = status >= 200 && status < 300 ? c.getInputStream() : c.getErrorStream();
            if (stream == null) throw new Exception("Sin respuesta del servidor");
            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            JSONObject resultado = new JSONObject(sb.toString());
            if (!resultado.optBoolean("success"))
                throw new Exception(resultado.optString("message", "Error del servidor"));
            return resultado;
        } finally {
            c.disconnect();
        }
    }

    private void cargar() {
        progreso.setVisibility(View.VISIBLE);
        estado.setVisibility(View.GONE);
        new Thread(() -> {
            try {
                JSONArray usuarios = solicitar("listar.php", null).optJSONArray("data");
                runOnUiThread(() -> mostrar(usuarios));
            } catch (Exception e) {
                runOnUiThread(() -> error(e.getMessage()));
            }
        }).start();
    }

    private void mostrar(JSONArray usuarios) {
        progreso.setVisibility(View.GONE);
        contenedor.removeAllViews();
        if (usuarios == null || usuarios.length() == 0) {
            estado.setVisibility(View.VISIBLE);
            estado.setText("No hay usuarios para mostrar.");
            return;
        }
        estado.setVisibility(View.GONE);
        for (int i = 0; i < usuarios.length(); i++) {
            JSONObject u = usuarios.optJSONObject(i);
            if (u != null) tarjeta(u);
        }
    }

    private void tarjeta(JSONObject u) {
        int id = u.optInt("id_usuario");
        boolean activo = u.optInt("activo") == 1;
        String rol = u.optString("rol", "USUARIO");
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(dp(14));
        fondo.setStroke(dp(1), Color.rgb(225, 233, 236));
        card.setBackground(fondo);
        card.setElevation(dp(2));
        LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
        cp.bottomMargin = dp(12);
        card.setLayoutParams(cp);
        card.addView(linea(u.optString("nombre") + " " + u.optString("apellido"), 17, texto, true));
        card.addView(linea("DNI: " + u.optString("dni"), 14, secundario, false));
        card.addView(linea("Rol: " + rol, 14, secundario, false));
        card.addView(linea(activo ? "● Activo" : "● Deshabilitado", 14, activo ? turquesa : fucsia, true));
        boolean propio = id == sesion.getInt("id_usuario", -1);
        boolean puedeEditar = "SUPERADMIN".equals(sesion.getString("rol", "")) || "USUARIO".equals(rol);
        if (puedeEditar) {
            LinearLayout acciones = new LinearLayout(this);
            acciones.setOrientation(LinearLayout.HORIZONTAL);
            acciones.setPadding(0, dp(10), 0, 0);
            AppCompatButton editar = boton("Editar", turquesa);
            acciones.addView(editar, new LinearLayout.LayoutParams(0, dp(44), 1));
            editar.setOnClickListener(v -> {
                Intent intent = new Intent(this, FormularioUsuarioActivity.class);
                intent.putExtra("id_usuario", id);
                intent.putExtra("nombre", u.optString("nombre"));
                intent.putExtra("apellido", u.optString("apellido"));
                intent.putExtra("dni", u.optString("dni"));
                intent.putExtra("rol", rol);
                startActivity(intent);
            });
            if (!propio) {
                AppCompatButton cambiar = boton(activo ? "Deshabilitar" : "Habilitar", fucsia);
                LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(0, dp(44), 1);
                bp.leftMargin = dp(8);
                acciones.addView(cambiar, bp);
                cambiar.setOnClickListener(v -> confirmarCambio(id, activo));
            }
            card.addView(acciones);
        }
        contenedor.addView(card);
    }

    private TextView linea(String valor, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(valor);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        t.setPadding(0, dp(4), 0, 0);
        return t;
    }

    private AppCompatButton boton(String valor, int color) {
        AppCompatButton b = new AppCompatButton(this);
        b.setText(valor);
        b.setTextSize(12);
        b.setTextColor(color);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), color);
        b.setBackground(bg);
        return b;
    }

    private void confirmarCambio(int id, boolean activo) {
        new AlertDialog.Builder(this)
                .setTitle(activo ? "Deshabilitar usuario" : "Habilitar usuario")
                .setMessage(activo ? "¿Querés deshabilitar esta cuenta?" : "¿Querés habilitar esta cuenta?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Confirmar", (d, w) -> {
                    progreso.setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        try {
                            JSONObject datos = new JSONObject();
                            datos.put("id_usuario", id);
                            datos.put("activo", activo ? 0 : 1);
                            solicitar("cambiar_estado.php", datos);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Estado actualizado", Toast.LENGTH_SHORT).show();
                                cargar();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> error(e.getMessage()));
                        }
                    }).start();
                }).show();
    }

    private void error(String msg) {
        progreso.setVisibility(View.GONE);
        estado.setVisibility(View.VISIBLE);
        estado.setText(msg);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private int dp(int n) {
        return Math.round(n * getResources().getDisplayMetrics().density);
    }
}