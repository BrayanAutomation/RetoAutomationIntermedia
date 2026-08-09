package com.reto.automation.api.utils;

/**
 * Configuración centralizada del API bajo prueba.
 * La URL base es parametrizable vía la propiedad de sistema "api.base.url"
 * (por ejemplo -Dapi.base.url=http://localhost:8080), evitando el hardcode
 * disperso observado en los proyectos de referencia.
 */
public final class ApiConstants {

    private ApiConstants() {
    }

    public static final String BASE_URL = System.getProperty("api.base.url", "http://localhost:8080");
}
