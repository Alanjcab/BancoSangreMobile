package com.example.bancosangremobile.controller;

import com.example.bancosangremobile.repository.GeorefRepository;

import org.json.JSONArray;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UbicacionController {

    private final GeorefRepository repository;
    private final ExecutorService executor;

    public UbicacionController() {
        repository = new GeorefRepository();
        executor = Executors.newSingleThreadExecutor();
    }

    public interface UbicacionCallback {
        void onSuccess(JSONArray datos);

        void onError(String mensaje);
    }

    public void obtenerProvincias(UbicacionCallback callback) {

        executor.execute(() -> {
            try {
                JSONArray provincias = repository.obtenerProvincias();

                callback.onSuccess(provincias);

            } catch (Exception e) {
                callback.onError(
                        "No se pudieron cargar las provincias: "
                                + e.getMessage()
                );
            }
        });
    }


    public void obtenerDepartamentos(
            String provinciaId,
            UbicacionCallback callback) {

        executor.execute(() -> {

            try {

                JSONArray departamentos =
                        repository.obtenerDepartamentos(provinciaId);

                callback.onSuccess(departamentos);

            } catch (Exception e) {

                callback.onError(
                        "No se pudieron cargar los partidos: "
                                + e.getMessage()
                );
            }
        });
    }


    public void obtenerLocalidades(
            String departamentoId,
            UbicacionCallback callback) {

        executor.execute(() -> {

            try {

                JSONArray localidades =
                        repository.obtenerLocalidades(departamentoId);

                callback.onSuccess(localidades);

            } catch (Exception e) {

                callback.onError(
                        "No se pudieron cargar las localidades: "
                                + e.getMessage()
                );
            }
        });
    }


    public void cerrar() {
        executor.shutdown();
    }
}
