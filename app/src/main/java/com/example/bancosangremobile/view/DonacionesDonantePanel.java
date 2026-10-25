package com.example.bancosangremobile.view;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.bancosangremobile.R;
import com.example.bancosangremobile.repository.DonacionRepository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class DonacionesDonantePanel {
    private final DetallePacienteActivity actividad;
    private final TextView resumen;
    private final TextView total;
    private final TextView advertencia;
    private final TextView sinDonaciones;
    private final LinearLayout historial;
    private final Spinner selectorPeriodo;
    private Resultado datosActuales;
    private final List<Periodo> periodos = new ArrayList<>();
    private boolean cargado;
    private boolean cargando;

    public DonacionesDonantePanel(DetallePacienteActivity actividad) {
        this.actividad = actividad;
        resumen = actividad.findViewById(R.id.txtResumenDonaciones);
        total = actividad.findViewById(R.id.txtTotalDonaciones);
        advertencia = actividad.findViewById(R.id.txtAdvertenciaDonaciones);
        sinDonaciones = actividad.findViewById(R.id.txtSinDonaciones);
        historial = actividad.findViewById(R.id.layoutUltimasDonaciones);
        selectorPeriodo = actividad.findViewById(R.id.spinnerPeriodoDonaciones);
        selectorPeriodo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (datosActuales != null && position >= 0 && position < periodos.size())
                    mostrarPeriodo();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public boolean haCargado() {
        return cargado;
    }

    public void cargar() {
        if (cargando) return;
        final int idDonante = actividad.getIntent().getIntExtra("id_donante", 0);
        if (idDonante <= 0) {
            mostrarError("No se pudo identificar al donante.");
            return;
        }
        cargando = true;
        cargado = true;
        resumen.setText("Cargando donaciones...");
        datosActuales = null;
        total.setText("Total: —");
        advertencia.setVisibility(View.GONE);
        sinDonaciones.setVisibility(View.GONE);
        historial.removeAllViews();

        new Thread(() -> {
            try {
                DonacionRepository repositorio = new DonacionRepository();
                JSONArray donaciones = repositorio.listarDonaciones();
                JSONArray donantes = repositorio.listarDonantes();
                String sexo = "";
                for (int i = 0; i < donantes.length(); i++) {
                    JSONObject d = donantes.optJSONObject(i);
                    if (d != null && d.optInt("id_donante", 0) == idDonante) {
                        sexo = d.optString("sexo", "").trim();
                        break;
                    }
                }
                final Resultado resultado = calcular(donaciones, idDonante, sexo);
                actividad.runOnUiThread(() -> {
                    cargando = false;
                    mostrar(resultado);
                });
            } catch (Exception e) {
                actividad.runOnUiThread(() -> {
                    cargando = false;
                    mostrarError("No se pudieron consultar las donaciones: " + e.getMessage());
                });
            }
        }).start();
    }

    private Resultado calcular(JSONArray datos, int idDonante, String sexo) {
        Resultado r = new Resultado();
        r.sexo = sexo;
        Calendar inicio = Calendar.getInstance();
        inicio.add(Calendar.MONTH, -12);
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.SECOND, 0);
        inicio.set(Calendar.MILLISECOND, 0);
        Date desde = inicio.getTime();
        Date hoy = Calendar.getInstance().getTime();

        for (int i = 0; i < datos.length(); i++) {
            JSONObject d = datos.optJSONObject(i);
            if (d == null || d.optInt("id_donante", 0) != idDonante) continue;
            int tipo = d.optInt("id_tipo_donacion", 0);
            if (tipo < 1 || tipo > 4) continue;
            String fechaTexto = d.optString("fecha_donacion", "");
            if (fechaTexto.length() < 10) continue;
            fechaTexto = fechaTexto.substring(0, 10);
            Date fecha = parsearFecha(fechaTexto);
            if (fecha == null || fecha.after(hoy)) continue;
            Registro registro = new Registro(d.optInt("id_donacion", 0), tipo, fecha, fechaTexto);
            r.registros.add(registro);

            // Las advertencias siempre usan los últimos 12 meses, sin importar el filtro.
            if (!fecha.before(desde)) r.cantidadesUltimos12[tipo]++;
            if (tipo == 1 && (r.ultimaSangre == null || fecha.after(r.ultimaSangre))) {
                r.ultimaSangre = fecha;
            }
            if (tipo != 1 && (r.ultimaAferesis == null || fecha.after(r.ultimaAferesis))) {
                r.ultimaAferesis = fecha;
            }
        }
        Collections.sort(r.registros, (a, b) -> {
            int cmp = b.fecha.compareTo(a.fecha);
            return cmp != 0 ? cmp : Integer.compare(b.id, a.id);
        });
        return r;
    }

    private void mostrar(Resultado r) {
        datosActuales = r;
        configurarPeriodos(r);
        mostrarPeriodo();

        String aviso = construirAviso(r);
        if (aviso.isEmpty()) {
            advertencia.setVisibility(View.GONE);
        } else {
            advertencia.setText("⚠ Advertencia de frecuencia\n\n" + aviso
                    + "\n\nEl personal sanitario debe evaluar cada nueva donación.");
            advertencia.setVisibility(View.VISIBLE);
        }
    }

    private void configurarPeriodos(Resultado r) {
        periodos.clear();
        periodos.add(new Periodo("Últimos 12 meses", 0, 0, 0));
        periodos.add(new Periodo("Historial completo", 1, 0, 0));
        Calendar hoy = Calendar.getInstance();
        int anioActual = hoy.get(Calendar.YEAR);
        int mesActual = hoy.get(Calendar.MONTH) + 1;
        int anioInicial = anioActual;
        for (Registro registro : r.registros) {
            Calendar c = Calendar.getInstance();
            c.setTime(registro.fecha);
            anioInicial = Math.min(anioInicial, c.get(Calendar.YEAR));
        }
        for (int anio = anioActual; anio >= anioInicial; anio--) {
            periodos.add(new Periodo("Año " + anio, 2, anio, 0));
        }
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (int anio = anioActual; anio >= anioInicial; anio--) {
            int ultimoMes = anio == anioActual ? mesActual : 12;
            for (int mes = ultimoMes; mes >= 1; mes--) {
                periodos.add(new Periodo(meses[mes - 1] + " " + anio, 3, anio, mes));
            }
        }
        List<String> nombres = new ArrayList<>();
        for (Periodo p : periodos) nombres.add(p.nombre);
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(actividad,
                android.R.layout.simple_spinner_item, nombres);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectorPeriodo.setAdapter(adaptador);
        selectorPeriodo.setSelection(0);
    }

    private boolean corresponde(Registro registro, Periodo periodo, Date desde) {
        if (periodo.tipo == 0) return !registro.fecha.before(desde);
        if (periodo.tipo == 1) return true;
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(registro.fecha);
        if (fecha.get(Calendar.YEAR) != periodo.anio) return false;
        return periodo.tipo == 2 || fecha.get(Calendar.MONTH) + 1 == periodo.mes;
    }

    private void mostrarPeriodo() {
        if (datosActuales == null || periodos.isEmpty()) return;
        int posicion = selectorPeriodo.getSelectedItemPosition();
        if (posicion < 0 || posicion >= periodos.size()) posicion = 0;
        Periodo periodo = periodos.get(posicion);
        Calendar inicio = Calendar.getInstance();
        inicio.add(Calendar.MONTH, -12);
        inicio.set(Calendar.HOUR_OF_DAY, 0);
        inicio.set(Calendar.MINUTE, 0);
        inicio.set(Calendar.SECOND, 0);
        inicio.set(Calendar.MILLISECOND, 0);
        Date desde = inicio.getTime();

        int[] cantidades = new int[5];
        List<Registro> filtrados = new ArrayList<>();
        for (Registro registro : datosActuales.registros) {
            if (!corresponde(registro, periodo, desde)) continue;
            cantidades[registro.tipo]++;
            filtrados.add(registro);
        }
        resumen.setText("Sangre                         " + cantidades[1]
                + "\nPlasma                         " + cantidades[2]
                + "\nPlaquetas                      " + cantidades[3]
                + "\nPlasma/Plaquetas          " + cantidades[4]);
        total.setText("Total: " + (cantidades[1] + cantidades[2] + cantidades[3] + cantidades[4]));
        historial.removeAllViews();
        sinDonaciones.setVisibility(filtrados.isEmpty() ? View.VISIBLE : View.GONE);
        int cantidad = Math.min(5, filtrados.size());
        for (int i = 0; i < cantidad; i++) {
            Registro registro = filtrados.get(i);
            TextView fila = new TextView(actividad);
            fila.setText(fechaVisual(registro.fechaTexto) + "     " + nombreTipo(registro.tipo));
            fila.setTextColor(0xFF163B4A);
            fila.setTextSize(15);
            fila.setPadding(dp(10), dp(12), dp(10), dp(12));
            fila.setBackgroundResource(i % 2 == 0 ? R.drawable.bg_dato_celeste : R.drawable.bg_home_card);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.bottomMargin = dp(8);
            historial.addView(fila, lp);
            if (registro.id > 0) {
                fila.setOnClickListener(v -> {
                    android.content.Intent intent = new android.content.Intent(
                            actividad, DetalleDonacionActivity.class);
                    intent.putExtra("id_donacion", registro.id);
                    actividad.startActivity(intent);
                });
            }
        }
    }

    private String construirAviso(Resultado r) {
        List<String> avisos = new ArrayList<>();
        int limite = 0;
        if ("Masculino".equalsIgnoreCase(r.sexo)) limite = 5;
        if ("Femenino".equalsIgnoreCase(r.sexo)) limite = 4;
        int sangre = r.cantidadesUltimos12[1];
        if (limite > 0 && sangre >= limite) {
            avisos.add("Sangre: se alcanzó o superó la referencia de " + limite
                    + " donaciones en 12 meses (registradas: " + sangre + ").");
        } else if (limite > 0 && sangre == limite - 1) {
            avisos.add("Sangre: próximo al límite de referencia (" + sangre
                    + " de " + limite + " en 12 meses).");
        } else if (limite == 0 && sangre > 0) {
            avisos.add("No se pudo verificar el sexo registrado para evaluar el límite anual de sangre.");
        }
        Date hoy = Calendar.getInstance().getTime();
        if (r.ultimaSangre != null) {
            Calendar fechaSugerida = Calendar.getInstance();
            fechaSugerida.setTime(r.ultimaSangre);
            fechaSugerida.add(Calendar.MONTH, 2);
            if (hoy.before(fechaSugerida.getTime())) {
                avisos.add("Sangre: todavía no transcurrieron 2 meses desde la última donación registrada ("
                        + formatear(r.ultimaSangre) + ").");
            }
        }
        if (r.ultimaAferesis != null) {
            Calendar fechaReferencia = Calendar.getInstance();
            fechaReferencia.setTime(r.ultimaAferesis);
            fechaReferencia.add(Calendar.DAY_OF_YEAR, 4);
            if (hoy.before(fechaReferencia.getTime())) {
                avisos.add("Aféresis: última sesión registrada el " + formatear(r.ultimaAferesis)
                        + ". Verificar que se cumplan 72 horas exactas y el protocolo aplicable.");
            }
        }
        if (avisos.isEmpty()) return "";
        StringBuilder texto = new StringBuilder();
        for (String aviso : avisos) {
            if (texto.length() > 0) texto.append("\n\n");
            texto.append("• ").append(aviso);
        }
        return texto.toString();
    }

    private void mostrarError(String mensaje) {
        resumen.setText(mensaje);
        total.setText("Total: —");
        advertencia.setVisibility(View.GONE);
        historial.removeAllViews();
        sinDonaciones.setVisibility(View.GONE);
    }

    private Date parsearFecha(String fecha) {
        try {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
            formato.setLenient(false);
            return formato.parse(fecha);
        } catch (Exception e) {
            return null;
        }
    }

    private String fechaVisual(String fecha) {
        return fecha.substring(8, 10) + "/" + fecha.substring(5, 7) + "/" + fecha.substring(0, 4);
    }

    private String formatear(Date fecha) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.ROOT).format(fecha);
    }

    private String nombreTipo(int tipo) {
        switch (tipo) {
            case 1:
                return "Sangre";
            case 2:
                return "Plasma";
            case 3:
                return "Plaquetas";
            case 4:
                return "Plasma/Plaquetas";
            default:
                return "Desconocida";
        }
    }

    private int dp(int valor) {
        return Math.round(valor * actividad.getResources().getDisplayMetrics().density);
    }

    private static final class Registro {
        final int id;
        final int tipo;
        final Date fecha;
        final String fechaTexto;

        Registro(int id, int tipo, Date fecha, String fechaTexto) {
            this.id = id;
            this.tipo = tipo;
            this.fecha = fecha;
            this.fechaTexto = fechaTexto;
        }
    }

    private static final class Periodo {
        final String nombre;
        final int tipo;
        final int anio;
        final int mes;

        Periodo(String nombre, int tipo, int anio, int mes) {
            this.nombre = nombre;
            this.tipo = tipo;
            this.anio = anio;
            this.mes = mes;
        }
    }

    private static final class Resultado {
        final int[] cantidadesUltimos12 = new int[5];
        final List<Registro> registros = new ArrayList<>();
        String sexo;
        Date ultimaSangre;
        Date ultimaAferesis;
    }
}