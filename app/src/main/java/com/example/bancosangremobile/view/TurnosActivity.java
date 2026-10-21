
package com.example.bancosangremobile.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;

public class TurnosActivity extends AppCompatActivity {

    private LinearLayout contenedorTurnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);

        contenedorTurnos = findViewById(R.id.contenedorTurnos);

        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnNuevoTurno)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(
                            TurnosActivity.this,
                            NuevoTurnoActivity.class
                    );
                    startActivity(intent);
                });

        cargarTurnosEjemplo();
    }

    private void cargarTurnosEjemplo() {

        agregarTurno(
                "Juan Pérez",
                "28.123.456",
                "20/10/2026 - 09:00",
                "Sangre",
                "Próximo"
        );

        agregarTurno(
                "María González",
                "32.456.789",
                "18/10/2026 - 14:30",
                "Aféresis (Plasma)",
                "Realizado"
        );

        agregarTurno(
                "Carlos Ramírez",
                "30.567.123",
                "25/10/2026 - 10:00",
                "Sangre",
                "Próximo"
        );

        agregarTurno(
                "Lucía Fernández",
                "27.890.234",
                "12/10/2026 - 11:00",
                "Sangre",
                "Cancelado"
        );

        agregarTurno(
                "Martín Rojas",
                "33.111.222",
                "05/10/2026 - 13:00",
                "Aféresis (Plaquetas)",
                "Realizado"
        );
    }

    private void agregarTurno(
            String nombre,
            String dni,
            String fechaHora,
            String tipo,
            String estado) {

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(dp(16), dp(13), dp(16), dp(13));

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(dp(18));
        fondo.setStroke(dp(1), Color.parseColor("#DDE8ED"));
        tarjeta.setBackground(fondo);

        LinearLayout.LayoutParams parametros =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        parametros.bottomMargin = dp(10);
        tarjeta.setLayoutParams(parametros);

        // Nombre y estado
        LinearLayout filaSuperior = new LinearLayout(this);
        filaSuperior.setOrientation(LinearLayout.HORIZONTAL);
        filaSuperior.setGravity(Gravity.CENTER_VERTICAL);

        TextView txtNombre = crearTexto(
                nombre, 15, "#163B4A", true
        );

        txtNombre.setLayoutParams(
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                )
        );

        TextView txtEstado = crearTexto(
                estado, 12, colorEstado(estado), true
        );

        txtEstado.setPadding(
                dp(10), dp(6), dp(10), dp(6)
        );

        GradientDrawable fondoEstado = new GradientDrawable();
        fondoEstado.setColor(fondoEstado(estado));
        fondoEstado.setCornerRadius(dp(20));
        txtEstado.setBackground(fondoEstado);

        filaSuperior.addView(txtNombre);
        filaSuperior.addView(txtEstado);

        tarjeta.addView(filaSuperior);

        // DNI
        TextView txtDni = crearTexto(
                "DNI " + dni, 13, "#597487", false
        );

        tarjeta.addView(txtDni);

        // Fecha y hora
        TextView txtFecha = crearTexto(
                "▦  " + fechaHora,
                13,
                "#163B4A",
                false
        );

        LinearLayout.LayoutParams margenFecha =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        margenFecha.topMargin = dp(8);
        txtFecha.setLayoutParams(margenFecha);
        tarjeta.addView(txtFecha);

        // Tipo de donación
        TextView txtTipo = crearTexto(
                "♥  " + tipo,
                13,
                "#163B4A",
                false
        );

        tarjeta.addView(txtTipo);


        tarjeta.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TurnosActivity.this,
                    DetalleTurnoActivity.class
            );

            intent.putExtra("nombre", nombre);
            intent.putExtra("dni", dni);
            String[] partesFechaHora = fechaHora.split(" - ", 2);
            intent.putExtra("fecha", partesFechaHora[0]);
            intent.putExtra("hora",
                    partesFechaHora.length > 1 ? partesFechaHora[1] : "");
            intent.putExtra("tipo", tipo);
            intent.putExtra("estado", estado);
            intent.putExtra("observaciones", "");

            startActivity(intent);
        });


        contenedorTurnos.addView(tarjeta);
    }

    private TextView crearTexto(
            String texto,
            int tamanio,
            String color,
            boolean negrita) {

        TextView textView = new TextView(this);

        textView.setText(texto);
        textView.setTextSize(tamanio);
        textView.setTextColor(Color.parseColor(color));

        if (negrita) {
            textView.setTypeface(
                    null,
                    Typeface.BOLD
            );
        }

        return textView;
    }

    private String colorEstado(String estado) {

        switch (estado) {
            case "Próximo":
                return "#1476C8";

            case "Realizado":
                return "#087B49";

            case "Cancelado":
                return "#D92D40";

            default:
                return "#163B4A";
        }
    }

    private int fondoEstado(String estado) {

        switch (estado) {
            case "Próximo":
                return Color.parseColor("#E1F2FF");

            case "Realizado":
                return Color.parseColor("#DDF8EA");

            case "Cancelado":
                return Color.parseColor("#FFE6EB");

            default:
                return Color.parseColor("#EEF3F5");
        }
    }

    private int dp(int valor) {
        return Math.round(
                valor * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
