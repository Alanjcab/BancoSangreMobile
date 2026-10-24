
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

import com.example.bancosangremobile.repository.TurnoRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bancosangremobile.R;

public class TurnosActivity extends AppCompatActivity {

    private LinearLayout contenedorTurnos;
    private EditText edtBuscarTurno;
    private TextView filtroTodos;
    private TextView filtroProximos;
    private TextView filtroRealizados;
    private TextView filtroCancelados;
    private JSONArray listaTurnos = new JSONArray();
    private String filtroActual = "TODOS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turnos);

        contenedorTurnos = findViewById(R.id.contenedorTurnos);

        edtBuscarTurno = findViewById(R.id.edtBuscarTurno);

        filtroTodos = findViewById(R.id.filtroTodos);
        filtroProximos = findViewById(R.id.filtroProximos);
        filtroRealizados = findViewById(R.id.filtroRealizados);
        filtroCancelados = findViewById(R.id.filtroCancelados);

        configurarBuscadorYFiltros();

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
    }

    private void configurarBuscadorYFiltros() {

        edtBuscarTurno.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

                mostrarTurnosFiltrados();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        filtroTodos.setOnClickListener(v -> cambiarFiltro("TODOS"));

        filtroProximos.setOnClickListener(v -> cambiarFiltro("PROXIMOS"));

        filtroRealizados.setOnClickListener(v -> cambiarFiltro("REALIZADOS"));

        filtroCancelados.setOnClickListener(v -> cambiarFiltro("CANCELADOS"));
    }

    private void cambiarFiltro(String nuevoFiltro) {

        filtroActual = nuevoFiltro;

        actualizarEstiloFiltros();

        mostrarTurnosFiltrados();
    }

    private void actualizarEstiloFiltros() {

        TextView[] filtros = {
                filtroTodos,
                filtroProximos,
                filtroRealizados,
                filtroCancelados
        };

        String[] valores = {
                "TODOS",
                "PROXIMOS",
                "REALIZADOS",
                "CANCELADOS"
        };

        for (int i = 0; i < filtros.length; i++) {

            boolean seleccionado = filtroActual.equals(valores[i]);

            filtros[i].setTextColor(
                    Color.parseColor(
                            seleccionado ? "#FF0066" : "#163B4A"
                    )
            );

            filtros[i].setTypeface(
                    null,
                    seleccionado ? Typeface.BOLD : Typeface.NORMAL
            );
        }
    }

    private void mostrarTurnosFiltrados() {

        contenedorTurnos.removeAllViews();

        String busqueda = edtBuscarTurno.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        int encontrados = 0;

        for (int i = 0; i < listaTurnos.length(); i++) {

            JSONObject turno = listaTurnos.optJSONObject(i);

            if (turno == null) {
                continue;
            }

            String nombre = (
                    turno.optString("nombre", "")
                            + " "
                            + turno.optString("apellido", "")
            ).trim();

            String dni = turno.optString("dni", "");

            String estado = turno.optString("estado", "")
                    .toUpperCase(Locale.ROOT);

            String textoBusqueda = (nombre + " " + dni)
                    .toLowerCase(Locale.ROOT);

            if (!textoBusqueda.contains(busqueda)) {
                continue;
            }

            boolean coincideFiltro;

            switch (filtroActual) {

                case "PROXIMOS":
                    coincideFiltro =
                            estado.equals("PROGRAMADO")
                                    || estado.equals("CONFIRMADO");
                    break;

                case "REALIZADOS":
                    coincideFiltro = estado.equals("REALIZADO");
                    break;

                case "CANCELADOS":
                    coincideFiltro = estado.equals("CANCELADO");
                    break;

                default:
                    coincideFiltro = true;
                    break;
            }

            if (!coincideFiltro) {
                continue;
            }

            String fecha = turno.optString("fecha_turno", "");
            String hora = turno.optString("hora_turno", "");
            String tipo = turno.optString("tipo_donacion", "");

            //conviero la fecha
            if (fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {

                String[] partes = fecha.split("-");

                fecha = partes[2] + "/"
                        + partes[1] + "/"
                        + partes[0];
            }

            if (hora.length() >= 5) {
                hora = hora.substring(0, 5);
            }

            String estadoVisual;

            switch (estado) {

                case "PROGRAMADO":
                case "CONFIRMADO":
                    estadoVisual = "Próximo";
                    break;

                case "REALIZADO":
                    estadoVisual = "Realizado";
                    break;

                case "CANCELADO":
                    estadoVisual = "Cancelado";
                    break;

                default:
                    estadoVisual = estado;
                    break;
            }

            //id del turno
            int idTurno = turno.optInt("id_turno", 0);
            int idDonante = turno.optInt("id_donante", 0);
            int idTipoDonacion = turno.optInt("id_tipo_donacion", 0);

            //traigo observaciones de la db
            String observaciones = turno.optString(
                    "observaciones",
                    ""
            );

            if (turno.isNull("observaciones")) {
                observaciones = "";
            }

            //crea tarjeta con los datos reales
            agregarTurno(
                    idTurno,
                    idDonante,
                    idTipoDonacion,
                    nombre,
                    dni,
                    fecha + " - " + hora,
                    tipo,
                    estadoVisual,
                    estado,
                    observaciones
            );

            encontrados++;
        }

        if (encontrados == 0) {

            contenedorTurnos.addView(
                    crearTexto(
                            "No se encontraron turnos.",
                            14,
                            "#597487",
                            false
                    )
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarTurnos();
    }

    private void cargarTurnos() {

        contenedorTurnos.removeAllViews();

        TextView cargando = crearTexto(
                "Cargando turnos...",
                14,
                "#597487",
                false
        );

        contenedorTurnos.addView(cargando);

        new Thread(() -> {

            try {

                TurnoRepository repository = new TurnoRepository();
                JSONArray turnos = repository.listarTurnos();

                runOnUiThread(() -> {
                    listaTurnos = turnos;
                    mostrarTurnosFiltrados();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    contenedorTurnos.removeAllViews();

                    mostrarError(
                            "No se pudieron cargar los turnos: "
                                    + e.getMessage()
                    );
                });
            }

        }).start();
    }

    private void mostrarError(String mensaje) {

        TextView error = crearTexto(
                mensaje,
                14,
                "#D92D40",
                false
        );

        contenedorTurnos.addView(error);

        Toast.makeText(
                this,
                mensaje,
                Toast.LENGTH_LONG
        ).show();
    }

    private void agregarTurno(
            int idTurno,
            int idDonante,
            int idTipoDonacion,
            String nombre,
            String dni,
            String fechaHora,
            String tipo,
            String estado,
            String estadoReal,
            String observaciones) {

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

        //nombre y estado
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

        //dni
        TextView txtDni = crearTexto(
                "DNI " + dni, 13, "#597487", false
        );

        tarjeta.addView(txtDni);

        //fecha y hora
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

        //tipo de donación
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
            intent.putExtra("id_turno", idTurno);
            intent.putExtra("id_donante", idDonante);
            intent.putExtra("id_tipo_donacion", idTipoDonacion);
            intent.putExtra("estado_real", estadoReal);
            intent.putExtra("nombre", nombre);
            intent.putExtra("dni", dni);
            String[] partesFechaHora = fechaHora.split(" - ", 2);
            intent.putExtra("fecha", partesFechaHora[0]);
            intent.putExtra("hora", partesFechaHora.length > 1 ? partesFechaHora[1] : "");
            intent.putExtra("tipo", tipo);
            intent.putExtra("estado", estado);
            intent.putExtra("observaciones", observaciones);

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
