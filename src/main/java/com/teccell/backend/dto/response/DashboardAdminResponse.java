package com.teccell.backend.dto.response;
import java.math.BigDecimal;
public record DashboardAdminResponse(
    long totalOrdenesActivas,
    long ordenesRecibidas,
    long ordenesEnDiagnostico,
    long ordenesEnReparacion,
    long ordenesParaRecoger,
    long ordenesVencidas,
    BigDecimal sumaPreciosAcordados,
    double tiempoPromedioReparacionDias
) {}
