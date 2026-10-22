
package com.example.bancosangremobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.bancosangremobile.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

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
}
