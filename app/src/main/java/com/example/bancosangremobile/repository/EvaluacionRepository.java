package com.example.bancosangremobile.repository;

import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EvaluacionRepository {

    public int crearEvaluacion(JSONObject datos) throws Exception {

        URL url = new URL(
                ApiConfig.getBaseUrl() + "evaluaciones/crear.php"
        );

        HttpURLConnection conexion =
                (HttpURLConnection) url.openConnection();

        try {
            conexion.setRequestMethod("POST");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);
            conexion.setDoOutput(true);
            conexion.setRequestProperty(
                    "Content-Type",
                    "application/json; charset=UTF-8"
            );

            byte[] cuerpo = datos.toString().getBytes(
                    StandardCharsets.UTF_8
            );

            try (java.io.OutputStream salida =
                         conexion.getOutputStream()) {
                salida.write(cuerpo);
            }

            int codigo = conexion.getResponseCode();

            InputStream flujo = codigo >= 200 && codigo < 300
                    ? conexion.getInputStream()
                    : conexion.getErrorStream();

            StringBuilder respuesta = new StringBuilder();

            if (flujo != null) {
                try (BufferedReader lector = new BufferedReader(
                        new InputStreamReader(
                                flujo,
                                StandardCharsets.UTF_8
                        ))) {

                    String linea;

                    while ((linea = lector.readLine()) != null) {
                        respuesta.append(linea);
                    }
                }
            }

            JSONObject json = new JSONObject(
                    respuesta.toString()
            );

            if (codigo < 200 || codigo >= 300 ||
                    !json.optBoolean("success", false)) {

                throw new Exception(
                        json.optString(
                                "message",
                                "No se pudo guardar la evaluación"
                        )
                );
            }

            return json.getInt("id_evaluacion");

        } finally {
            conexion.disconnect();
        }
    }

    public JSONArray listarEvaluaciones(int idDonante) throws Exception {

        URL url = new URL(
                ApiConfig.getBaseUrl()
                        + "evaluaciones/listar.php?id_donante="
                        + idDonante
        );

        HttpURLConnection conexion =
                (HttpURLConnection) url.openConnection();

        try {
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            int codigo = conexion.getResponseCode();

            if (codigo != HttpURLConnection.HTTP_OK) {
                throw new Exception(
                        "Error al consultar las evaluaciones: " + codigo
                );
            }

            StringBuilder respuesta = new StringBuilder();

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(
                            conexion.getInputStream(),
                            StandardCharsets.UTF_8
                    )
            )) {
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }
            }

            JSONObject json = new JSONObject(respuesta.toString());

            if (!json.optBoolean("success", false)) {
                throw new Exception(
                        json.optString(
                                "message",
                                "No se pudieron obtener las evaluaciones"
                        )
                );
            }

            return json.getJSONArray("data");

        } finally {
            conexion.disconnect();
        }
    }
}
