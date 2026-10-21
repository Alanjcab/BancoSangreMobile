package com.example.bancosangremobile.repository;

import com.example.bancosangremobile.model.Donante;
import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.List;

public class DonanteRepository {

    public List<Donante> listarDonantes() throws Exception {

        List<Donante> donantes = new ArrayList<>();

        String direccion =
                ApiConfig.getDonantesUrl("listar.php");

        JSONObject respuesta = ejecutarGet(direccion);

        JSONArray datos = respuesta.getJSONArray("data");

        for (int i = 0; i < datos.length(); i++) {

            JSONObject fila = datos.getJSONObject(i);

            int id = fila.getInt("id_donante");

            String nombre = textoOpcional(
                    fila, "nombre"
            );

            String apellido = textoOpcional(
                    fila, "apellido"
            );

            String dni = textoOpcional(
                    fila, "dni"
            );

            String grupo = textoOpcional(
                    fila, "grupo_sanguineo"
            );

            String rh = textoOpcional(
                    fila, "factor_rh"
            );

            String nombreCompleto =
                    (nombre + " " + apellido).trim();

            String grupoCompleto = "Sin datos";

            if (!grupo.isEmpty()) {
                grupoCompleto = grupo + rh;
            }

            // La API de donantes todavía no
            // proporciona la aptitud médica.
            String estado = "Sin evaluar";

            donantes.add(
                    new Donante(
                            id,
                            nombreCompleto,
                            dni,
                            grupoCompleto,
                            estado
                    )
            );
        }

        return donantes;
    }

    public int crearDonante(JSONObject datos)
            throws Exception {

        String direccion =
                ApiConfig.getDonantesUrl("crear.php");

        JSONObject respuesta = ejecutarPost(
                direccion,
                datos
        );

        int idDonante = respuesta.optInt(
                "id_donante",
                0
        );

        if (idDonante <= 0) {
            throw new Exception(
                    "El servidor no devolvió el ID del donante"
            );
        }

        return idDonante;
    }

    public JSONObject obtenerDonantePorId(
            int idDonante
    ) throws Exception {

        if (idDonante <= 0) {
            throw new IllegalArgumentException(
                    "El ID del donante no es válido"
            );
        }

        String direccion =
                ApiConfig.getDonantesUrl("obtener.php")
                        + "?id=" + idDonante;

        JSONObject respuesta = ejecutarGet(direccion);

        JSONObject donante =
                respuesta.optJSONObject("data");

        if (donante == null) {
            throw new Exception(
                    "La API no devolvió los datos del donante"
            );
        }

        return donante;
    }

    public void actualizarDonante(
            JSONObject datos
    ) throws Exception {

        int idDonante = datos.optInt(
                "id_donante",
                0
        );

        if (idDonante <= 0) {
            throw new IllegalArgumentException(
                    "El ID del donante es obligatorio"
            );
        }

        String direccion =
                ApiConfig.getDonantesUrl("actualizar.php");

        ejecutarPost(direccion, datos);
    }

    private JSONObject ejecutarGet(
            String direccion
    ) throws Exception {

        HttpURLConnection conexion = null;

        try {

            URL url = new URL(direccion);

            conexion = (HttpURLConnection)
                    url.openConnection();

            conexion.setRequestMethod("GET");

            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            conexion.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            return procesarRespuesta(conexion);

        } finally {

            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    private JSONObject ejecutarPost(
            String direccion,
            JSONObject datos
    ) throws Exception {

        HttpURLConnection conexion = null;

        try {

            URL url = new URL(direccion);

            conexion = (HttpURLConnection)
                    url.openConnection();

            conexion.setRequestMethod("POST");

            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            conexion.setDoOutput(true);

            conexion.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            conexion.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            byte[] contenido =
                    datos.toString().getBytes(
                            StandardCharsets.UTF_8
                    );

            try (OutputStream salida =
                         conexion.getOutputStream()) {

                salida.write(contenido);
            }

            return procesarRespuesta(conexion);

        } finally {

            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    private JSONObject procesarRespuesta(
            HttpURLConnection conexion
    ) throws Exception {

        int codigo = conexion.getResponseCode();

        InputStream entrada =
                codigo >= 200 && codigo < 300
                        ? conexion.getInputStream()
                        : conexion.getErrorStream();

        if (entrada == null) {
            throw new IOException(
                    "El servidor no devolvió una respuesta"
            );
        }

        StringBuilder respuesta = new StringBuilder();

        try (BufferedReader lector =
                     new BufferedReader(
                             new InputStreamReader(
                                     entrada,
                                     StandardCharsets.UTF_8
                             )
                     )) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                respuesta.append(linea);
            }
        }

        JSONObject json;

        try {

            json = new JSONObject(
                    respuesta.toString()
            );

        } catch (JSONException e) {

            throw new IOException(
                    "El servidor devolvió una respuesta inválida",
                    e
            );
        }

        if (codigo < 200 || codigo >= 300
                || !json.optBoolean("success", false)) {

            throw new Exception(
                    json.optString(
                            "message",
                            "Error HTTP " + codigo
                    )
            );
        }

        return json;
    }

    private String textoOpcional(
            JSONObject json,
            String campo
    ) {

        if (json.isNull(campo)) {
            return "";
        }

        return json.optString(
                campo,
                ""
        ).trim();
    }
}
