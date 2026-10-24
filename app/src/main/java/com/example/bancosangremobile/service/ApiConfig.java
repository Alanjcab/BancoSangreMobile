package com.example.bancosangremobile.service;

public final class ApiConfig {

    private static final String BASE_URL = "http://10.0.2.2/banco_sangre_api/";

    //evita crear instacias de clases
    private ApiConfig() {
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static String getDonantesUrl(String archivo) {
        return BASE_URL + "donantes/" + archivo;
    }

    public static String getUsuariosUrl(String archivo) {
        return BASE_URL + "usuarios/" + archivo;
    }
}
