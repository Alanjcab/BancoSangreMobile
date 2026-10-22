package com.example.bancosangremobile.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FormularioUsuarioActivity extends AppCompatActivity {
    private EditText nombre, apellido, dni, clave;
    private Spinner rol;
    private AppCompatButton guardar;
    private ProgressBar progreso;
    private int idUsuario;
    private String rolOriginal;
    private String[] roles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_usuario);
        nombre = findViewById(R.id.edtNombreUsuario);
        apellido = findViewById(R.id.edtApellidoUsuario);
        dni = findViewById(R.id.edtDniUsuario);
        clave = findViewById(R.id.edtClaveUsuario);
        rol = findViewById(R.id.spinnerRolUsuario);
        guardar = findViewById(R.id.btnGuardarUsuario);
        progreso = findViewById(R.id.progresoGuardarUsuario);
        findViewById(R.id.btnVolverFormulario).setOnClickListener(v -> finish());
        idUsuario = getIntent().getIntExtra("id_usuario", 0);
        rolOriginal = getIntent().getStringExtra("rol");
        SharedPreferences pref = getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE);
        boolean superadmin = "SUPERADMIN".equals(pref.getString("rol", ""));
        roles = superadmin ? new String[]{"USUARIO", "ADMIN", "SUPERADMIN"} : new String[]{"USUARIO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rol.setAdapter(adapter);
        if (idUsuario > 0) {
            ((TextView) findViewById(R.id.txtTituloFormulario)).setText("Editar usuario");
            ((TextView) findViewById(R.id.txtAyudaClave)).setVisibility(View.VISIBLE);
            nombre.setText(getIntent().getStringExtra("nombre"));
            apellido.setText(getIntent().getStringExtra("apellido"));
            dni.setText(getIntent().getStringExtra("dni"));
            for (int i = 0; i < roles.length; i++)
                if (roles[i].equals(rolOriginal)) rol.setSelection(i);
            if (idUsuario == pref.getInt("id_usuario", -1)) rol.setEnabled(false);
        }
        guardar.setOnClickListener(v -> enviar());
    }

    private void enviar() {
        String n = nombre.getText().toString().trim(), a = apellido.getText().toString().trim();
        String d = dni.getText().toString().trim(), p = clave.getText().toString();
        if (n.isEmpty()) {
            nombre.setError("Ingresá el nombre");
            return;
        }
        if (a.isEmpty()) {
            apellido.setError("Ingresá el apellido");
            return;
        }
        if (!d.matches("[0-9]{7,9}")) {
            dni.setError("DNI de 7 a 9 dígitos");
            return;
        }
        if ((idUsuario == 0 || !p.isEmpty()) && p.length() < 6) {
            clave.setError("Mínimo 6 caracteres");
            return;
        }
        String r = (String) rol.getSelectedItem();
        guardar.setEnabled(false);
        progreso.setVisibility(View.VISIBLE);
        new Thread(() -> {
            HttpURLConnection c = null;
            try {
                SharedPreferences pref = getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE);
                String token = pref.getString("token", "");
                if (token.isEmpty()) throw new Exception("Iniciá sesión nuevamente");
                JSONObject datos = new JSONObject();
                if (idUsuario > 0) datos.put("id_usuario", idUsuario);
                datos.put("nombre", n);
                datos.put("apellido", a);
                datos.put("dni", d);
                datos.put("rol", r);
                if (idUsuario == 0 || !p.isEmpty()) datos.put("password", p);
                String archivo = idUsuario > 0 ? "editar.php" : "crear.php";
                c = (HttpURLConnection) new URL(ApiConfig.getUsuariosUrl(archivo)).openConnection();
                c.setRequestMethod("POST");
                c.setDoOutput(true);
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                c.setRequestProperty("Authorization", "Bearer " + token);
                c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream os = c.getOutputStream()) {
                    os.write(datos.toString().getBytes(StandardCharsets.UTF_8));
                }
                int status = c.getResponseCode();
                InputStream stream = status >= 200 && status < 300 ? c.getInputStream() : c.getErrorStream();
                if (stream == null) throw new Exception("Sin respuesta del servidor");
                StringBuilder sb = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) sb.append(line);
                }
                JSONObject respuesta = new JSONObject(sb.toString());
                if (!respuesta.optBoolean("success"))
                    throw new Exception(respuesta.optString("message", "No se pudo guardar"));
                runOnUiThread(() -> {
                    Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                String msg = e.getMessage();
                runOnUiThread(() -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
            } finally {
                if (c != null) c.disconnect();
                runOnUiThread(() -> {
                    guardar.setEnabled(true);
                    progreso.setVisibility(View.GONE);
                });
            }
        }).start();
    }
}
