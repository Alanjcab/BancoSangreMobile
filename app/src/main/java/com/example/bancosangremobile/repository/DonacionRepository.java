package com.example.bancosangremobile.repository;

import com.example.bancosangremobile.service.ApiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DonacionRepository {
    private JSONObject solicitar(String ruta, JSONObject datos) throws Exception {
        HttpURLConnection conexion = null;
        try {
            conexion = (HttpURLConnection) new URL(ApiConfig.getBaseUrl() + ruta).openConnection();
            conexion.setConnectTimeout(10000);
            conexion.setReadTimeout(10000);
            conexion.setRequestProperty("Accept", "application/json");
            if (datos != null) {
                conexion.setRequestMethod("POST");
                conexion.setDoOutput(true);
                conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                try (OutputStream salida = conexion.getOutputStream()) {
                    salida.write(datos.toString().getBytes(StandardCharsets.UTF_8));
                }
            } else {
                conexion.setRequestMethod("GET");
            }
            int codigo = conexion.getResponseCode();
            InputStream entrada = codigo >= 200 && codigo < 300
                    ? conexion.getInputStream() : conexion.getErrorStream();
            if (entrada == null) throw new Exception("El servidor no respondió");
            StringBuilder contenido = new StringBuilder();
            try (BufferedReader lector = new BufferedReader(
                    new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
                String linea;
                while ((linea = lector.readLine()) != null) contenido.append(linea);
            }
            JSONObject respuesta = new JSONObject(contenido.toString());
            if (codigo < 200 || codigo >= 300 || !respuesta.optBoolean("success", false)) {
                throw new Exception(respuesta.optString("message", "Error del servidor"));
            }
            return respuesta;
        } finally {
            if (conexion != null) conexion.disconnect();
        }
    }

    public JSONArray listarDonaciones() throws Exception {
        JSONArray lista = solicitar("donaciones/listar.php", null).optJSONArray("data");
        if (lista == null) throw new Exception("Respuesta de donaciones inválida");
        return lista;
    }

    public JSONArray listarDonantes() throws Exception {
        JSONArray lista = solicitar("donantes/listar.php", null).optJSONArray("data");
        if (lista == null) throw new Exception("Respuesta de donantes inválida");
        return lista;
    }

    public int crearDonacion(JSONObject datos) throws Exception {
        int id = solicitar("donaciones/crear.php", datos).optInt("id_donacion", 0);
        if (id <= 0) throw new Exception("No se recibió el ID de la donación");
        return id;
    }

    public JSONObject obtenerDonacion(int id) throws Exception {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        JSONObject dato = solicitar("donaciones/obtener.php?id_donacion=" + id, null)
                .optJSONObject("data");
        if (dato == null) throw new Exception("No se encontraron los datos de la donación");
        return dato;
    }
}
