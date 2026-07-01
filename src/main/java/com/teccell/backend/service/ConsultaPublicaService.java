package com.teccell.backend.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.teccell.backend.dto.response.ConsultaPublicaResponse;
import com.teccell.backend.dto.response.RecuperacionTicketResponse;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.repository.OrdenReparacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsultaPublicaService {
    private final OrdenReparacionRepository ordenRepository;


    public ConsultaPublicaResponse consultarPorTicket(String ticket) {
        OrdenReparacion orden = ordenRepository.findByTicket(ticket)
                .orElseThrow(() -> new RuntimeException("No se encontró ninguna orden con el ticket: " + ticket));

        if (!orden.getActivo()) {
            throw new RuntimeException("La orden solicitada no está activa.");
        }

        ConsultaPublicaResponse response = new ConsultaPublicaResponse();
        response.setTicket(orden.getTicket());
        response.setTipoEquipo(orden.getEquipo().getTipo().name()); 
        response.setMarcaModelo(orden.getEquipo().getMarca() + " " + orden.getEquipo().getModelo()); 
        response.setFallaReportada(orden.getFallaReportada());
        response.setDiagnostico(orden.getDiagnostico());
        response.setEstado(orden.getEstado().name());
        response.setPrecioAcordado(orden.getPrecioAcordado().doubleValue());
        response.setFechaIngreso(orden.getFechaIngreso().toLocalDate());
        response.setFechaEstimada(orden.getFechaEstimadaEntrega());
        

        String nombreCompleto = orden.getEquipo().getCliente().getNombres() + " " + orden.getEquipo().getCliente().getApellidos();
        response.setClienteOculto(ofuscarNombre(nombreCompleto));

        
        response.setLineaTiempo(List.of()); 

        return response;
    }


    public List<RecuperacionTicketResponse> recuperarPorTelefono(String telefono) {
        return ordenRepository.findByClienteTelefono(telefono).stream()
                .map(o -> new RecuperacionTicketResponse(o.getTicket(), o.getEquipo().getMarca(), o.getEstado().name()))
                .collect(Collectors.toList());
    }

    
    public List<RecuperacionTicketResponse> recuperarPorDniParcial(String ultimos4) {
        return ordenRepository.findByClienteDniTerminaCon(ultimos4).stream()
                .map(o -> new RecuperacionTicketResponse(o.getTicket(), o.getEquipo().getMarca(), o.getEstado().name()))
                .collect(Collectors.toList());
    }

    
    public List<RecuperacionTicketResponse> recuperarPorDniYTelefono(String ultimos4, String telefono) {
        return ordenRepository.findByClienteDniTerminaConAndTelefono(ultimos4, telefono).stream()
                .map(o -> new RecuperacionTicketResponse(o.getTicket(), o.getEquipo().getMarca(), o.getEstado().name()))
                .collect(Collectors.toList());
    }

    private String ofuscarNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return "Cliente Anonimizado";
        String[] partes = nombreCompleto.split(" ");
        StringBuilder resultado = new StringBuilder();
        for (String parte : partes) {
            if (!parte.isBlank()) {
                resultado.append(parte.charAt(0)).append("*** ");
            }
        }
        return resultado.toString().trim();
    }
}
