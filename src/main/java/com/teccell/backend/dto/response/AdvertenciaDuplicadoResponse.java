package com.teccell.backend.dto.response;

import java.util.List;

public record AdvertenciaDuplicadoResponse(
        boolean existeDni,
        boolean existeTelefono,
        ClienteResponse clientePorDni,
        List<ClienteResponse> clientesPorTelefono,
        String mensaje
) {
}