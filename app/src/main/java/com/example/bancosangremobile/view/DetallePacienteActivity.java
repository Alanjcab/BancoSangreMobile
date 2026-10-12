package com.example.bancosangremobile.view;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bancosangremobile.R;

public class DetallePacienteActivity extends AppCompatActivity {

    private TextView tabInformacion;
    private TextView tabSalud;
    private TextView tabTurnos;
    private TextView tabDonaciones;

    private View seccionInformacion;
    private View seccionSalud;
    private View seccionTurnos;
    private View seccionDonaciones;

    private final int COLOR_ACTIVO = 0xFFF7065D;
    private final int COLOR_INACTIVO = 0xFF527080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_paciente);

        // Volver
        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        // Editar: pantalla pendiente de implementar
        TextView btnEditar = findViewById(R.id.btnEditarDonante);
        btnEditar.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        "Próximamente: editar donante",
                        Toast.LENGTH_SHORT
                ).show()
        );

        // Referencias a las pestañas
        tabInformacion = findViewById(R.id.tabInformacion);
        tabSalud = findViewById(R.id.tabSalud);
        tabTurnos = findViewById(R.id.tabTurnos);
        tabDonaciones = findViewById(R.id.tabDonaciones);

        // Referencias a los contenidos
        seccionInformacion = findViewById(R.id.seccionInformacion);
        seccionSalud = findViewById(R.id.seccionSalud);
        seccionTurnos = findViewById(R.id.seccionTurnos);
        seccionDonaciones = findViewById(R.id.seccionDonaciones);

        // Eventos
        tabInformacion.setOnClickListener(v -> mostrarSeccion(0));
        tabSalud.setOnClickListener(v -> mostrarSeccion(1));
        tabTurnos.setOnClickListener(v -> mostrarSeccion(2));
        tabDonaciones.setOnClickListener(v -> mostrarSeccion(3));

        // Pestaña inicial
        mostrarSeccion(0);

        // Ajustes de barras del sistema
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

    private void mostrarSeccion(int seleccion) {

        View[] secciones = {
                seccionInformacion,
                seccionSalud,
                seccionTurnos,
                seccionDonaciones
        };

        TextView[] pestañas = {
                tabInformacion,
                tabSalud,
                tabTurnos,
                tabDonaciones
        };

        for (int i = 0; i < secciones.length; i++) {

            boolean activa = i == seleccion;

            secciones[i].setVisibility(
                    activa ? View.VISIBLE : View.GONE
            );

            pestañas[i].setTextColor(
                    activa ? COLOR_ACTIVO : COLOR_INACTIVO
            );

            pestañas[i].setTypeface(
                    null,
                    activa
                            ? android.graphics.Typeface.BOLD
                            : android.graphics.Typeface.NORMAL
            );
        }
    }
}
