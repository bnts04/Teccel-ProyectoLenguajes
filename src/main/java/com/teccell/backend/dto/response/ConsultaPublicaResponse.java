package com.teccell.backend.dto.response;
import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ConsultaPublicaResponse {
    private String ticket;
    private String clienteOculto; 
    private String tipoEquipo;
    private String marcaModelo;
    private String fallaReportada;
    private String diagnostico;
    private String estado;
    private Double precioAcordado;
    private LocalDate fechaIngreso;
    private LocalDate fechaEstimada;
    private List<HistorialPublicoOrdenResponse> lineaTiempo;
}
