package com.teccell.backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CrearClienteRequest(

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 80, message = "Los nombres no deben superar los 80 caracteres")
        String nombres,

        @NotBlank(message = "Los apellidos son obligatorios")
        @Size(max = 80, message = "Los apellidos no deben superar los 80 caracteres")
        String apellidos,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
        String telefono,

        @Size(max = 15, message = "El DNI no debe superar los 15 caracteres")
        String dni,

        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120, message = "El correo no debe superar los 120 caracteres")
        String correo,

        @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
        String direccion
) {
}