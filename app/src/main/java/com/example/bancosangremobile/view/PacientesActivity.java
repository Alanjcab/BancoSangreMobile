package com.example.bancosangremobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.adapter.DonanteAdapter;
import com.example.bancosangremobile.model.Donante;

import java.util.ArrayList;
import java.util.List;


public class PacientesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pacientes);

        findViewById(R.id.btnNuevoDonante).setOnClickListener(v -> {
            Intent intent = new Intent(PacientesActivity.this, NuevoDonanteActivity.class);
            startActivity(intent);
        });

        // Botón para volver al Home
        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        // Configuración del listado
        RecyclerView recyclerDonantes =
                findViewById(R.id.recyclerDonantes);

        recyclerDonantes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Datos de prueba (más adelante vendrán de la API)
        List<Donante> donantes = new ArrayList<>();

        donantes.add(new Donante(
                "Juan Pérez",
                "28.123.456",
                "A+",
                "Apto"
        ));

        donantes.add(new Donante(
                "María González",
                "32.456.789",
                "O+",
                "No apto"
        ));

        donantes.add(new Donante(
                "Carlos Ramírez",
                "30.567.123",
                "B+",
                "En evaluación"
        ));

        // Conectar el adaptador
        DonanteAdapter adapter = new DonanteAdapter(
                donantes,
                donante -> {
                    Intent intent = new Intent(
                            PacientesActivity.this,
                            DetallePacienteActivity.class
                    );

                    startActivity(intent);
                }
        );

        recyclerDonantes.setAdapter(adapter);

        // Ajuste para las barras del sistema
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
}
