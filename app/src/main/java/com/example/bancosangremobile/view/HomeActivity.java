
package com.example.bancosangremobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.SharedPreferences;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import com.example.bancosangremobile.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // BOTÓN CERRAR SESIÓN
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            cerrarSesion();
        });

        SharedPreferences preferencias = getSharedPreferences(
                "sesion_banco_sangre",
                MODE_PRIVATE
        );

        String nombre = preferencias.getString("nombre", "");

        TextView txtSaludo = findViewById(R.id.txtSaludo);

        if (nombre != null && !nombre.trim().isEmpty()) {
            String primerNombre = nombre.trim().split("\\s+")[0];
            txtSaludo.setText("¡Hola, " + primerNombre + "!");
        } else {
            txtSaludo.setText("¡Hola!");
        }

        LinearLayout cardDonantes =
                findViewById(R.id.cardDonantes);

        cardDonantes.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    PacientesActivity.class
            );

            startActivity(intent);
        });

        LinearLayout cardTurnos =
                findViewById(R.id.cardTurnos);

        cardTurnos.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    TurnosActivity.class
            );

            startActivity(intent);
        });


        LinearLayout cardDonaciones = findViewById(R.id.cardDonaciones);

        cardDonaciones.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    DonacionesActivity.class
            );
            startActivity(intent);
        });

        LinearLayout cardUsuarios =
                findViewById(R.id.cardUsuarios);

        cardUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    UsuariosActivity.class
            );

            startActivity(intent);
        });


        findViewById(R.id.navInicio).setOnClickListener(v -> {
        });

        findViewById(R.id.navDonantes).setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    PacientesActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.navTurnos).setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    TurnosActivity.class
            );

            startActivity(intent);
        });

        findViewById(R.id.navUsuarios).setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    UsuariosActivity.class
            );

            startActivity(intent);
        });

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

    private void cerrarSesion() {

        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Querés cerrar tu sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {

                    // Eliminar los datos de la sesión
                    getSharedPreferences(
                            "sesion_banco_sangre",
                            MODE_PRIVATE
                    ).edit().clear().apply();

                    // Volver al Login
                    Intent intent = new Intent(
                            HomeActivity.this,
                            LoginActivity.class
                    );

                    // Eliminar el historial de pantallas
                    intent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK |
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
