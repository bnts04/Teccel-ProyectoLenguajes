package com.teccell.backend.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teccell.backend.dto.response.ConsultaPublicaResponse;
import com.teccell.backend.dto.response.RecuperacionTicketResponse;
import com.teccell.backend.service.ConsultaPublicaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/publico/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConsultaPublicaController {
    private final ConsultaPublicaService consultaPublicaService;


    @GetMapping("/ticket/{ticket}")
    public ResponseEntity<ConsultaPublicaResponse> consultarPorTicket(@PathVariable String ticket) {
        return ResponseEntity.ok(consultaPublicaService.consultarPorTicket(ticket));
    }


    @GetMapping("/recuperar/telefono")
    public ResponseEntity<List<RecuperacionTicketResponse>> recuperarPorTelefono(@RequestParam("numero") String numero) {
        return ResponseEntity.ok(consultaPublicaService.recuperarPorTelefono(numero));
    }


    @GetMapping("/recuperar/dni")
    public ResponseEntity<List<RecuperacionTicketResponse>> recuperarPorDniParcial(@RequestParam("ultimos4") String ultimos4) {
        return ResponseEntity.ok(consultaPublicaService.recuperarPorDniParcial(ultimos4));
    }

    @GetMapping("/recuperar")
    public ResponseEntity<List<RecuperacionTicketResponse>> recuperarPorDniYTelefono(
            @RequestParam("ultimos4") String ultimos4,
            @RequestParam("telefono") String telefono) {
        return ResponseEntity.ok(consultaPublicaService.recuperarPorDniYTelefono(ultimos4, telefono));
    }
}
