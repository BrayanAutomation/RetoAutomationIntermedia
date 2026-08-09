package com.reto.automation.ui.models;

import lombok.Data;

/**
 * Datos de un nuevo usuario a registrar mediante el formulario web.
 */
@Data
public class NewUser {

    private final String name;
    private final String email;
    private final String role;
}
