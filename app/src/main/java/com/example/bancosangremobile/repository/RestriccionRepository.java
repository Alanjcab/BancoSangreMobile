package com.example.bancosangremobile.repository;

import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;
import java.io.InputStream;

public class RestriccionRepository {

    public JSONArray listarMotivos() throws Exception {

        URL url = new URL(
                ApiConfig.getBaseUrl()
                        + "restricciones/listar_motivos.php"
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
                        "Error al consultar los motivos: " + codigo
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
                                "No se pudieron obtener los motivos"
                        )
                );
            }

            return json.getJSONArray("data");

        } finally {
            conexion.disconnect();
        }
    }


    public JSONArray listarRestricciones(int idDonante) throws Exception {

        URL url = new URL(
                ApiConfig.getBaseUrl()
                        + "restricciones/listar.php?id_donante="
                        + idDonante
        );

        HttpURLConnection conexion =
                (HttpURLConnection) url.openConnection();

        try {
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            int codigo = conexion.getResponseCode();

            InputStream flujo = codigo >= 200 && codigo < 300
                    ? conexion.getInputStream()
                    : conexion.getErrorStream();

            if (flujo == null) {
                throw new Exception("No se recibió respuesta del servidor");
            }

            StringBuilder contenido = new StringBuilder();

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(
                            flujo,
                            StandardCharsets.UTF_8
                    )
            )) {
                String linea;

                while ((linea = lector.readLine()) != null) {
                    contenido.append(linea);
                }
            }

            JSONObject respuesta = new JSONObject(contenido.toString());

            if (codigo < 200 || codigo >= 300
                    || !respuesta.optBoolean("success", false)) {

                throw new Exception(
                        respuesta.optString(
                                "message",
                                "No se pudieron cargar las restricciones"
                        )
                );
            }

            return respuesta.getJSONArray("data");

        } finally {
            conexion.disconnect();
        }
    }


    public int crearMotivo(String nombre) throws Exception {
        JSONObject datos = new JSONObject();
        datos.put("nombre", nombre);

        JSONObject respuesta = enviarPost(
                "restricciones/crear_motivo.php",
                datos
        );

        return respuesta.getInt("id_motivo_restriccion");
    }

    public int crearRestriccion(JSONObject datos) throws Exception {
        JSONObject respuesta = enviarPost(
                "restricciones/crear.php",
                datos
        );

        return respuesta.getInt("id_restriccion");
    }


    public void finalizarRestriccion(int idRestriccion) throws Exception {

        JSONObject datos = new JSONObject();
        datos.put("id_restriccion", idRestriccion);

        enviarPost(
                "restricciones/finalizar.php",
                datos
        );
    }


    public void editarRestriccion(JSONObject datos) throws Exception {

        enviarPost(
                "restricciones/editar.php",
                datos
        );
    }


    private JSONObject enviarPost(String ruta, JSONObject datos)
            throws Exception {

        URL url = new URL(ApiConfig.getBaseUrl() + ruta);

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

            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(cuerpo);
            }

            int codigo = conexion.getResponseCode();

            InputStream flujo = codigo >= 200 && codigo < 300
                    ? conexion.getInputStream()
                    : conexion.getErrorStream();

            StringBuilder contenido = new StringBuilder();

            if (flujo != null) {
                try (BufferedReader lector = new BufferedReader(
                        new InputStreamReader(
                                flujo,
                                StandardCharsets.UTF_8
                        )
                )) {
                    String linea;

                    while ((linea = lector.readLine()) != null) {
                        contenido.append(linea);
                    }
                }
            }

            JSONObject respuesta = new JSONObject(contenido.toString());

            if (codigo < 200 || codigo >= 300
                    || !respuesta.optBoolean("success", false)) {

                throw new Exception(
                        respuesta.optString(
                                "message",
                                "No se pudo guardar la información"
                        )
                );
            }

            return respuesta;

        } finally {
            conexion.disconnect();
        }
    }

}
