
package com.example.bancosangremobile.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;

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
import java.util.Locale;

public class PacientesActivity extends AppCompatActivity {

    private RecyclerView recyclerDonantes;
    private DonanteRepository donanteRepository;
    private EditText edtBuscarPaciente;
    private LinearLayout contenedorFiltroActivo;
    private TextView txtFiltroActivo;
    private TextView btnQuitarFiltro;

    private TextView filtroTodos;
    private TextView filtroAptos;
    private TextView filtroNoAptos;
    private TextView filtroRestriccion;

    private final List<Donante> todosLosDonantes = new ArrayList<>();

    private String filtroSeleccionado = "Todos";
    private String grupoSeleccionado = "Todos";
    private String rhSeleccionado = "Todos";

    private static final int COLOR_ACTIVO = Color.rgb(247, 6, 93);
    private static final int COLOR_INACTIVO = Color.rgb(82, 112, 128);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pacientes);

        donanteRepository = new DonanteRepository();

        findViewById(R.id.btnNuevoDonante).setOnClickListener(v -> {
            Intent intent = new Intent(
                    PacientesActivity.this,
                    NuevoDonanteActivity.class
            );
            startActivity(intent);
        });

        TextView btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(v -> finish());

        recyclerDonantes = findViewById(R.id.recyclerDonantes);
        recyclerDonantes.setLayoutManager(
                new LinearLayoutManager(this)
        );

        edtBuscarPaciente = findViewById(R.id.edtBuscarPaciente);

        contenedorFiltroActivo = findViewById(R.id.contenedorFiltroActivo);
        txtFiltroActivo = findViewById(R.id.txtFiltroActivo);
        btnQuitarFiltro = findViewById(R.id.btnQuitarFiltro);

        btnQuitarFiltro.setOnClickListener(v -> {
            grupoSeleccionado = "Todos";
            rhSeleccionado = "Todos";
            filtroSeleccionado = "Todos";

            actualizarEstiloFiltros();
            aplicarFiltros();
            actualizarEtiquetaFiltro();
        });

        filtroTodos = findViewById(R.id.filtroTodos);
        filtroAptos = findViewById(R.id.filtroAptos);
        filtroNoAptos = findViewById(R.id.filtroNoAptos);
        filtroRestriccion = findViewById(R.id.filtroRestriccion);

        filtroTodos.setOnClickListener(v -> seleccionarFiltro("Todos"));
        filtroAptos.setOnClickListener(v -> seleccionarFiltro("Apto"));
        filtroNoAptos.setOnClickListener(v -> seleccionarFiltro("No apto"));
        filtroRestriccion.setOnClickListener(
                v -> seleccionarFiltro("Con restricción")
        );

        findViewById(R.id.btnFiltros).setOnClickListener(
                v -> abrirVentanaFiltros()
        );

        edtBuscarPaciente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after
            ) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count
            ) {
                aplicarFiltros();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        actualizarEstiloFiltros();

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

    private void seleccionarFiltro(String filtro) {
        filtroSeleccionado = filtro;
        actualizarEstiloFiltros();
        aplicarFiltros();
        actualizarEtiquetaFiltro();
    }

    private void actualizarEstiloFiltros() {
        configurarEstilo(filtroTodos, "Todos");
        configurarEstilo(filtroAptos, "Apto");
        configurarEstilo(filtroNoAptos, "No apto");
        configurarEstilo(filtroRestriccion, "Con restricción");
    }

    private void configurarEstilo(TextView vista, String filtro) {
        boolean seleccionado = filtroSeleccionado.equals(filtro);

        vista.setTextColor(
                seleccionado ? COLOR_ACTIVO : COLOR_INACTIVO
        );

        vista.setTypeface(
                null,
                seleccionado ? Typeface.BOLD : Typeface.NORMAL
        );
    }

    private void abrirVentanaFiltros() {

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_filtros_donantes);

        Window ventana = dialog.getWindow();

        if (ventana != null) {
            ventana.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }

        RadioButton[] opcionesGrupo = {
                dialog.findViewById(R.id.rbGrupoTodos),
                dialog.findViewById(R.id.rbGrupoA),
                dialog.findViewById(R.id.rbGrupoB),
                dialog.findViewById(R.id.rbGrupoAB),
                dialog.findViewById(R.id.rbGrupoO)
        };

        String[] valoresGrupo = {
                "Todos", "A", "B", "AB", "O"
        };

        RadioButton[] opcionesRh = {
                dialog.findViewById(R.id.rbRhTodos),
                dialog.findViewById(R.id.rbRhPositivo),
                dialog.findViewById(R.id.rbRhNegativo)
        };

        String[] valoresRh = {
                "Todos", "+", "-"
        };

        RadioButton[] opcionesAptitud = {
                dialog.findViewById(R.id.rbAptitudTodos),
                dialog.findViewById(R.id.rbAptos),
                dialog.findViewById(R.id.rbNoAptos),
                dialog.findViewById(R.id.rbConRestriccion)
        };

        String[] valoresAptitud = {
                "Todos", "Apto", "No apto", "Con restricción"
        };

        seleccionarOpcion(
                opcionesGrupo,
                valoresGrupo,
                grupoSeleccionado
        );

        seleccionarOpcion(
                opcionesRh,
                valoresRh,
                rhSeleccionado
        );

        seleccionarOpcion(
                opcionesAptitud,
                valoresAptitud,
                filtroSeleccionado
        );

        configurarOpciones(opcionesGrupo);
        configurarOpciones(opcionesRh);
        configurarOpciones(opcionesAptitud);

        Button btnLimpiar = dialog.findViewById(
                R.id.btnLimpiarFiltros
        );

        Button btnAplicar = dialog.findViewById(
                R.id.btnAplicarFiltros
        );

        btnLimpiar.setOnClickListener(v -> {
            seleccionarOpcion(
                    opcionesGrupo, valoresGrupo, "Todos"
            );

            seleccionarOpcion(
                    opcionesRh, valoresRh, "Todos"
            );

            seleccionarOpcion(
                    opcionesAptitud, valoresAptitud, "Todos"
            );
        });

        btnAplicar.setOnClickListener(v -> {

            grupoSeleccionado = obtenerSeleccion(
                    opcionesGrupo, valoresGrupo
            );

            rhSeleccionado = obtenerSeleccion(
                    opcionesRh, valoresRh
            );

            filtroSeleccionado = obtenerSeleccion(
                    opcionesAptitud, valoresAptitud
            );

            actualizarEstiloFiltros();
            aplicarFiltros();
            actualizarEtiquetaFiltro();

            dialog.dismiss();
        });

        dialog.show();

        if (ventana != null) {
            ventana.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.92),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void configurarOpciones(RadioButton[] opciones) {

        for (RadioButton opcion : opciones) {

            opcion.setOnClickListener(v -> {

                for (RadioButton otra : opciones) {
                    otra.setChecked(otra == opcion);
                }
            });
        }
    }

    private void seleccionarOpcion(
            RadioButton[] opciones,
            String[] valores,
            String seleccionado
    ) {

        for (int i = 0; i < opciones.length; i++) {
            opciones[i].setChecked(
                    valores[i].equals(seleccionado)
            );
        }
    }

    private String obtenerSeleccion(
            RadioButton[] opciones,
            String[] valores
    ) {

        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].isChecked()) {
                return valores[i];
            }
        }

        return "Todos";
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

                    todosLosDonantes.clear();
                    todosLosDonantes.addAll(donantes);

                    aplicarFiltros();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    if (isFinishing() || isDestroyed()) {
                        return;
                    }

                    Toast.makeText(
                            PacientesActivity.this,
                            "Error al cargar donantes: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
            }

        }).start();
    }

    private void aplicarFiltros() {

        List<Donante> donantesFiltrados = new ArrayList<>();

        String busqueda = edtBuscarPaciente.getText()
                .toString()
                .trim();

        for (Donante donante : todosLosDonantes) {

            boolean coincideEstado =
                    filtroSeleccionado.equals("Todos")
                            || filtroSeleccionado.equalsIgnoreCase(
                            donante.getEstado()
                    );

            boolean coincideDni =
                    donante.getDni() != null
                            && donante.getDni().contains(busqueda);

            String grupoCompleto = donante.getGrupoSanguineo();

            if (grupoCompleto == null) {
                grupoCompleto = "";
            }

            grupoCompleto = grupoCompleto
                    .trim()
                    .toUpperCase(Locale.ROOT);

            String grupoBase = grupoCompleto
                    .replace("+", "")
                    .replace("-", "");

            boolean coincideGrupo =
                    grupoSeleccionado.equals("Todos")
                            || grupoSeleccionado.equals(grupoBase);

            boolean coincideRh =
                    rhSeleccionado.equals("Todos")
                            || grupoCompleto.endsWith(rhSeleccionado);

            if (coincideEstado
                    && coincideDni
                    && coincideGrupo
                    && coincideRh) {

                donantesFiltrados.add(donante);
            }
        }

        mostrarDonantes(donantesFiltrados);
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

    private void actualizarEtiquetaFiltro() {

        List<String> filtros = new ArrayList<>();

        if (!grupoSeleccionado.equals("Todos")) {
            filtros.add("Grupo: " + grupoSeleccionado);
        }

        if (!rhSeleccionado.equals("Todos")) {
            filtros.add("Rh: " + rhSeleccionado);
        }

        if (!filtroSeleccionado.equals("Todos")) {
            filtros.add(filtroSeleccionado);
        }

        if (filtros.isEmpty()) {
            contenedorFiltroActivo.setVisibility(View.GONE);
        } else {
            txtFiltroActivo.setText(
                    android.text.TextUtils.join(" · ", filtros)
            );

            contenedorFiltroActivo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (donanteRepository != null) {
            cargarDonantes();
        }
    }
}
