package com.teccell.backend.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecuperacionTicketResponse {
    private String ticket;
    private String tipoEquipo;
    private String estado;
}
