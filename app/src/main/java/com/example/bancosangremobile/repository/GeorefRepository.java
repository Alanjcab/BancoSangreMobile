package com.example.bancosangremobile.repository;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class GeorefRepository {

    private static final String BASE_URL =
            "https://apis.datos.gob.ar/georef/api/";

    public JSONArray obtenerProvincias() throws Exception {

        String direccion = BASE_URL + "provincias?campos=id,nombre";

        HttpURLConnection conexion = null;

        try {
            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            int codigoRespuesta = conexion.getResponseCode();

            if (codigoRespuesta != HttpURLConnection.HTTP_OK) {
                throw new Exception(
                        "Error al consultar Georef: " + codigoRespuesta
                );
            }

            InputStream entrada = conexion.getInputStream();

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(
                            entrada,
                            StandardCharsets.UTF_8
                    ))) {

                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONObject json = new JSONObject(respuesta.toString());

                return json.getJSONArray("provincias");
            }

        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }


    public JSONArray obtenerDepartamentos(String provinciaId) throws Exception {

        String idCodificado = URLEncoder.encode(
                provinciaId,
                "UTF-8"
        );

        String direccion = BASE_URL
                + "departamentos?provincia="
                + idCodificado
                + "&campos=id,nombre&max=1000";

        HttpURLConnection conexion = null;

        try {
            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            if (conexion.getResponseCode()
                    != HttpURLConnection.HTTP_OK) {

                throw new Exception(
                        "Error al consultar los departamentos"
                );
            }

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(
                            conexion.getInputStream(),
                            StandardCharsets.UTF_8
                    ))) {

                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONObject json = new JSONObject(
                        respuesta.toString()
                );

                return json.getJSONArray("departamentos");
            }

        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }


    public JSONArray obtenerLocalidades(String departamentoId) throws Exception {

        String idCodificado = URLEncoder.encode(
                departamentoId,
                "UTF-8"
        );

        String direccion = BASE_URL
                + "localidades?departamento="
                + idCodificado
                + "&campos=id,nombre&max=5000";

        HttpURLConnection conexion = null;

        try {
            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            if (conexion.getResponseCode()
                    != HttpURLConnection.HTTP_OK) {

                throw new Exception(
                        "Error al consultar las localidades"
                );
            }

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(
                            conexion.getInputStream(),
                            StandardCharsets.UTF_8
                    ))) {

                StringBuilder respuesta = new StringBuilder();
                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }

                JSONObject json = new JSONObject(
                        respuesta.toString()
                );

                return json.getJSONArray("localidades");
            }

        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }


}
