package com.teccell.backend.dto;

import java.util.List;

public record AdvertenciaDuplicadoResponse(
        boolean existeDni,
        boolean existeTelefono,
        ClienteResponse clientePorDni,
        List<ClienteResponse> clientesPorTelefono,
        String mensaje
) {
}