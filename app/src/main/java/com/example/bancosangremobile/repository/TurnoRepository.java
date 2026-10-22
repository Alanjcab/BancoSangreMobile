package com.example.bancosangremobile.repository;

import com.example.bancosangremobile.service.ApiConfig;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import java.io.IOException;

public class TurnoRepository {

    public int crearTurno(JSONObject datos) throws Exception {

        String direccion =
                ApiConfig.getBaseUrl() + "turnos/crear.php";

        HttpURLConnection conexion = null;

        try {

            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();

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

            byte[] contenido = datos.toString().getBytes(
                    StandardCharsets.UTF_8
            );

            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(contenido);
            }

            int codigo = conexion.getResponseCode();

            InputStream entrada =
                    codigo >= 200 && codigo < 300
                            ? conexion.getInputStream()
                            : conexion.getErrorStream();

            if (entrada == null) {
                throw new Exception("El servidor no devolvió una respuesta");
            }

            StringBuilder respuesta = new StringBuilder();

            try (BufferedReader lector = new BufferedReader(
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

            JSONObject json = new JSONObject(respuesta.toString());

            if (codigo < 200 || codigo >= 300
                    || !json.optBoolean("success", false)) {

                throw new Exception(
                        json.optString(
                                "message",
                                "Error al guardar el turno"
                        )
                );
            }

            int idTurno = json.optInt("id_turno", 0);

            if (idTurno <= 0) {
                throw new Exception(
                        "El servidor no devolvió el ID del turno"
                );
            }

            return idTurno;

        } finally {

            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    public JSONArray listarTurnos() throws Exception {

        String direccion =
                ApiConfig.getBaseUrl() + "turnos/listar.php";

        HttpURLConnection conexion = null;

        try {

            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();

            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            conexion.setRequestProperty(
                    "Accept",
                    "application/json"
            );

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

            try (BufferedReader lector = new BufferedReader(
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

            JSONObject json = new JSONObject(respuesta.toString());

            if (codigo < 200 || codigo >= 300
                    || !json.optBoolean("success", false)) {

                throw new Exception(
                        json.optString(
                                "message",
                                "Error al obtener los turnos"
                        )
                );
            }

            JSONArray turnos = json.optJSONArray("data");

            if (turnos == null) {
                throw new Exception(
                        "La API no devolvió una lista de turnos"
                );
            }

            return turnos;

        } finally {

            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    public JSONObject obtenerTurnoPorId(int idTurno) throws Exception {

        String direccion = ApiConfig.getBaseUrl()
                + "turnos/obtener.php?id=" + idTurno;

        HttpURLConnection conexion = null;

        try {
            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);

            int codigo = conexion.getResponseCode();

            InputStream entrada = codigo >= 200 && codigo < 300
                    ? conexion.getInputStream()
                    : conexion.getErrorStream();

            if (entrada == null) {
                throw new Exception("El servidor no devolvió una respuesta");
            }

            StringBuilder respuesta = new StringBuilder();

            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(entrada, StandardCharsets.UTF_8))) {

                String linea;

                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);
                }
            }

            JSONObject json = new JSONObject(respuesta.toString());

            if (codigo < 200 || codigo >= 300
                    || !json.optBoolean("success", false)) {

                throw new Exception(
                        json.optString("message", "Error al obtener el turno")
                );
            }

            JSONObject turno = json.optJSONObject("data");

            if (turno == null) {
                throw new Exception("No se encontraron datos del turno");
            }

            return turno;

        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    public void actualizarTurno(JSONObject datos) throws Exception {

        String direccion =
                ApiConfig.getBaseUrl() + "turnos/actualizar.php";

        HttpURLConnection conexion = null;

        try {
            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();
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

            byte[] contenido = datos.toString().getBytes(
                    StandardCharsets.UTF_8
            );

            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(contenido);
            }

            int codigo = conexion.getResponseCode();

            InputStream entrada =
                    codigo >= 200 && codigo < 300
                            ? conexion.getInputStream()
                            : conexion.getErrorStream();

            if (entrada == null) {
                throw new Exception("El servidor no devolvió una respuesta");
            }

            StringBuilder respuesta = new StringBuilder();

            try (BufferedReader lector = new BufferedReader(
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

            JSONObject json = new JSONObject(respuesta.toString());

            if (codigo < 200 || codigo >= 300
                    || !json.optBoolean("success", false)) {

                throw new Exception(
                        json.optString(
                                "message",
                                "Error al actualizar el turno"
                        )
                );
            }

        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    public void cancelarTurno(int idTurno) throws Exception {

        String direccion =
                ApiConfig.getBaseUrl() + "turnos/cancelar.php";

        HttpURLConnection conexion = null;

        try {
            URL url = new URL(direccion);

            conexion = (HttpURLConnection) url.openConnection();
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

            JSONObject datos = new JSONObject();
            datos.put("id_turno", idTurno);

            byte[] contenido = datos.toString().getBytes(
                    StandardCharsets.UTF_8
            );

            try (OutputStream salida = conexion.getOutputStream()) {
                salida.write(contenido);
            }

            int codigo = conexion.getResponseCode();

            InputStream entrada =
                    codigo >= 200 && codigo < 300
                            ? conexion.getInputStream()
                            : conexion.getErrorStream();

            if (entrada == null) {
                throw new Exception(
                        "El servidor no devolvió una respuesta"
                );
            }

            StringBuilder respuesta = new StringBuilder();

            try (BufferedReader lector = new BufferedReader(
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

            JSONObject json = new JSONObject(respuesta.toString());

            if (codigo < 200 || codigo >= 300
                    || !json.optBoolean("success", false)) {

                throw new Exception(
                        json.optString(
                                "message",
                                "Error al cancelar el turno"
                        )
                );
            }

        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }
}