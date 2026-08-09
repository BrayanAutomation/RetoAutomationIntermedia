package com.reto.automation.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa el cuerpo de una petición de creación/actualización de usuario
 * hacia POST/PUT /api/users.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String name;
    private String email;
    private String role;
    private String status;
}
