package com.teccell.backend.dto.response;
import java.time.LocalDate;

import com.teccell.backend.enums.SituacionEntrega;
public record AlertaOrdenResponse (
    Long id,
    String ticket,
    String marcaModelo,
    String clienteNombre,
    LocalDate fechaEstimadaEntrega,
    long diasDiferencia,
    SituacionEntrega situacion
)
    
{}
