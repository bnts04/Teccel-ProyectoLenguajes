package com.teccell.backend.controller;

import com.teccell.backend.dto.ActualizarClienteRequest;
import com.teccell.backend.dto.AdvertenciaDuplicadoResponse;
import com.teccell.backend.dto.ClienteResponse;
import com.teccell.backend.dto.CrearClienteRequest;
import com.teccell.backend.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ClienteResponse crearCliente(@Valid @RequestBody CrearClienteRequest request) {
        return clienteService.crearCliente(request);
    }

    @GetMapping
    public List<ClienteResponse> listarClientes() {
        return clienteService.listarClientes();
    }

    @GetMapping("/{id}")
    public ClienteResponse obtenerCliente(@PathVariable Long id) {
        return clienteService.obtenerCliente(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarClienteRequest request
    ) {
        return clienteService.actualizarCliente(id, request);
    }

    @GetMapping("/buscar")
    public List<ClienteResponse> buscarClientes(@RequestParam(required = false) String texto) {
        return clienteService.buscarClientes(texto);
    }

    @GetMapping("/buscar/telefono")
    public List<ClienteResponse> buscarPorTelefono(@RequestParam String telefono) {
        return clienteService.buscarPorTelefono(telefono);
    }

    @GetMapping("/buscar/dni")
    public ClienteResponse buscarPorDni(@RequestParam String dni) {
        return clienteService.buscarPorDni(dni);
    }

    @GetMapping("/verificar-duplicados")
    public AdvertenciaDuplicadoResponse verificarDuplicados(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String telefono
    ) {
        return clienteService.verificarDuplicados(dni, telefono);
    }
}