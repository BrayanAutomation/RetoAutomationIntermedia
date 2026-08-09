package com.reto.automation.ui.utils;

import java.util.UUID;

/**
 * Generador de datos de prueba reutilizable, evita colisiones cuando el
 * mismo escenario se ejecuta varias veces contra una base de datos que
 * persiste entre corridas.
 */
public final class TestDataProvider {

    private TestDataProvider() {
    }

    public static String uniqueEmail(String localPart) {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return localPart + "+" + suffix + "@example.com";
    }
}
