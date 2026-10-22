package com.example.bancosangremobile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsuario;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        edtUsuario = findViewById(R.id.edtUsuario);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> iniciarSesion());

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );
    }

    private void iniciarSesion() {

        String dni = edtUsuario.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (dni.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Ingresá tu DNI y contraseña",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (!dni.matches("\\d{7,9}")) {
            edtUsuario.setError("Ingresá un DNI válido");
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Ingresando...");

        new Thread(() -> {

            HttpURLConnection conexion = null;

            try {
                URL url = new URL(
                        ApiConfig.getUsuariosUrl("login.php")
                );

                conexion = (HttpURLConnection) url.openConnection();

                conexion.setRequestMethod("POST");
                conexion.setConnectTimeout(10000);
                conexion.setReadTimeout(10000);
                conexion.setDoOutput(true);

                conexion.setRequestProperty(
                        "Content-Type",
                        "application/json; charset=UTF-8"
                );

                conexion.setRequestProperty(
                        "Accept",
                        "application/json"
                );

                JSONObject datos = new JSONObject();
                datos.put("dni", dni);
                datos.put("password", password);

                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(
                            datos.toString().getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );
                }

                int codigo = conexion.getResponseCode();

                InputStream flujo = codigo >= 200 && codigo < 300
                        ? conexion.getInputStream()
                        : conexion.getErrorStream();

                if (flujo == null) {
                    throw new Exception(
                            "El servidor no devolvió una respuesta."
                    );
                }

                StringBuilder respuesta = new StringBuilder();

                try (BufferedReader lector = new BufferedReader(
                        new InputStreamReader(
                                flujo,
                                StandardCharsets.UTF_8
                        )
                )) {
                    String linea;

                    while ((linea = lector.readLine()) != null) {
                        respuesta.append(linea);
                    }
                }

                JSONObject json = new JSONObject(
                        respuesta.toString()
                );

                if (json.optBoolean("success", false)) {

                    String token = json.getString("token");
                    JSONObject usuario = json.getJSONObject("usuario");

                    String rol = usuario.getString("rol");
                    String nombre = usuario.getString("nombre");

                    // Guardamos la sesión para las próximas
                    // solicitudes a la API.
                    SharedPreferences preferencias =
                            getSharedPreferences(
                                    "sesion_banco_sangre",
                                    MODE_PRIVATE
                            );

                    boolean guardado = preferencias.edit()
                            .putString("token", token)
                            .putString("rol", rol)
                            .putString("nombre", nombre)
                            .putInt(
                                    "id_usuario",
                                    usuario.getInt("id_usuario")
                            )
                            .commit();

                    if (!guardado) {
                        throw new Exception(
                                "No se pudo guardar la sesión."
                        );
                    }

                    runOnUiThread(() -> {

                        Toast.makeText(
                                LoginActivity.this,
                                "¡Bienvenido, " + nombre + "!",
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent intent = new Intent(
                                LoginActivity.this,
                                HomeActivity.class
                        );

                        intent.addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );

                        startActivity(intent);
                        finish();
                    });

                } else {

                    String mensaje = json.optString(
                            "message",
                            "No se pudo iniciar sesión."
                    );

                    runOnUiThread(() -> {
                        Toast.makeText(
                                LoginActivity.this,
                                mensaje,
                                Toast.LENGTH_LONG
                        ).show();

                        restaurarBoton();
                    });
                }

            } catch (Exception e) {

                runOnUiThread(() -> {
                    Toast.makeText(
                            LoginActivity.this,
                            "Error de conexión: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    restaurarBoton();
                });

            } finally {

                if (conexion != null) {
                    conexion.disconnect();
                }
            }

        }).start();
    }

    private void restaurarBoton() {
        btnLogin.setEnabled(true);
        btnLogin.setText("INICIAR SESIÓN");
    }
}
