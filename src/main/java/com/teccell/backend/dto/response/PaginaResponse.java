package com.teccell.backend.dto.response;

import java.util.List;

public record PaginaResponse<T>(
        List<T> contenido,
        int paginaActual,
        int tamanioPagina,
        long totalElementos,
        int totalPaginas,
        boolean esPrimera,
        boolean esUltima
) {
}