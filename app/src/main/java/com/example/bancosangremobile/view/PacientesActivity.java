package com.example.bancosangremobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.bancosangremobile.repository.DonanteRepository;

import java.util.ArrayList;
import java.util.List;

public class PacientesActivity extends AppCompatActivity {

    private RecyclerView recyclerDonantes;
    private DonanteRepository donanteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pacientes);

        donanteRepository = new DonanteRepository();

        // BOTÓN NUEVO DONANTE
        findViewById(R.id.btnNuevoDonante)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            PacientesActivity.this,
                            NuevoDonanteActivity.class
                    );

                    startActivity(intent);
                });

        // BOTÓN VOLVER
        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        // RECYCLERVIEW
        recyclerDonantes = findViewById(
                R.id.recyclerDonantes
        );

        recyclerDonantes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // CONSULTAR MYSQL MEDIANTE LA API PHP
        cargarDonantes();

        // AJUSTE DE BARRAS DEL SISTEMA
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

    private void cargarDonantes() {

        new Thread(() -> {

            try {

                List<Donante> donantes =
                        donanteRepository.listarDonantes();

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    mostrarDonantes(donantes);
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Toast.makeText(
                            PacientesActivity.this,
                            "Error al cargar donantes: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    mostrarDonantes(new ArrayList<>());
                });
            }

        }).start();
    }

    private void mostrarDonantes(List<Donante> donantes) {

        DonanteAdapter adapter = new DonanteAdapter(
                donantes,
                donante -> {

                    Intent intent = new Intent(
                            PacientesActivity.this,
                            DetallePacienteActivity.class
                    );

                    intent.putExtra(
                            "id_donante",
                            donante.getIdDonante()
                    );

                    intent.putExtra(
                            "nombre",
                            donante.getNombre()
                    );

                    intent.putExtra(
                            "dni",
                            donante.getDni()
                    );

                    intent.putExtra(
                            "grupo",
                            donante.getGrupoSanguineo()
                    );

                    intent.putExtra(
                            "aptitud",
                            donante.getEstado()
                    );

                    startActivity(intent);
                }
        );

        recyclerDonantes.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Actualizar listado al regresar a esta pantalla.
        if (donanteRepository != null) {
            cargarDonantes();
        }
    }
}
