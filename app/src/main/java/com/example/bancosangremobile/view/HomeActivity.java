package com.example.bancosangremobile.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bancosangremobile.R;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());
        SharedPreferences preferencias = getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE);
        String nombre = preferencias.getString("nombre", "");
        TextView saludo = findViewById(R.id.txtSaludo);
        saludo.setText(nombre == null || nombre.trim().isEmpty() ? "¡Hola!" :
                "¡Hola, " + nombre.trim().split("\\s+")[0] + "!");
        findViewById(R.id.cardDonantes).setOnClickListener(v -> abrir(PacientesActivity.class));
        findViewById(R.id.cardTurnos).setOnClickListener(v -> abrir(TurnosActivity.class));
        findViewById(R.id.cardDonaciones).setOnClickListener(v -> abrir(DonacionesActivity.class));
        findViewById(R.id.cardUsuarios).setOnClickListener(v -> abrir(UsuariosActivity.class));
        findViewById(R.id.navInicio).setOnClickListener(v -> {
        });
        findViewById(R.id.navDonantes).setOnClickListener(v -> abrir(PacientesActivity.class));
        findViewById(R.id.navTurnos).setOnClickListener(v -> abrir(TurnosActivity.class));
        findViewById(R.id.navUsuarios).setOnClickListener(v -> abrir(UsuariosActivity.class));
        String rol = preferencias.getString("rol", "").trim().toUpperCase(Locale.ROOT);
        View estadisticas = findViewById(R.id.cardEstadisticas);
        boolean permitido = rol.equals("ADMIN") || rol.equals("SUPERADMIN");
        estadisticas.setVisibility(permitido ? View.VISIBLE : View.GONE);
        if (permitido) estadisticas.setOnClickListener(v -> abrir(EstadisticasActivity.class));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void abrir(Class<?> pantalla) {
        startActivity(new Intent(this, pantalla));
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this).setTitle("Cerrar sesión")
                .setMessage("¿Querés cerrar tu sesión?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    getSharedPreferences("sesion_banco_sangre", MODE_PRIVATE).edit().clear().apply();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }).show();
    }
}
