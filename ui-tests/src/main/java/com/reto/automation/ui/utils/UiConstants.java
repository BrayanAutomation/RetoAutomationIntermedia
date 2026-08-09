package com.reto.automation.ui.utils;

/**
 * Configuración centralizada del frontend bajo prueba.
 * La URL base es parametrizable vía la propiedad de sistema "ui.base.url"
 * (por ejemplo -Dui.base.url=http://localhost:3000), evitando el hardcode
 * disperso observado en el proyecto de referencia.
 */
public final class UiConstants {

    private UiConstants() {
    }

    public static final String BASE_URL = System.getProperty("ui.base.url", "http://localhost:3000");
}
