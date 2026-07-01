package com.teccell.backend.dto.response;
import java.time.LocalDateTime;

import lombok.Data;
@Data
public class HistorialPublicoOrdenResponse {
    private String evento;
    private String notas;
    private LocalDateTime fechaEvento;
}
