package com.teccell.backend.service;

import com.teccell.backend.dto.ActualizarClienteRequest;
import com.teccell.backend.dto.AdvertenciaDuplicadoResponse;
import com.teccell.backend.dto.ClienteResponse;
import com.teccell.backend.dto.CrearClienteRequest;
import com.teccell.backend.entity.Cliente;
import com.teccell.backend.exception.BusinessException;
import com.teccell.backend.exception.ResourceNotFoundException;
import com.teccell.backend.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteResponse crearCliente(CrearClienteRequest request) {
        String dni = limpiarOpcional(request.dni());

        if (dni != null && clienteRepository.existsByDni(dni)) {
            throw new BusinessException("Ya existe un cliente registrado con este DNI");
        }

        Cliente cliente = Cliente.builder()
                .nombres(limpiarObligatorio(request.nombres()))
                .apellidos(limpiarObligatorio(request.apellidos()))
                .telefono(limpiarObligatorio(request.telefono()))
                .dni(dni)
                .correo(limpiarOpcional(request.correo()))
                .direccion(limpiarOpcional(request.direccion()))
                .activo(true)
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        return convertirAResponse(guardado);
    }

    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ClienteResponse obtenerCliente(Long id) {
        Cliente cliente = buscarClientePorId(id);
        return convertirAResponse(cliente);
    }

    public ClienteResponse actualizarCliente(Long id, ActualizarClienteRequest request) {
        Cliente cliente = buscarClientePorId(id);

        String dniNuevo = limpiarOpcional(request.dni());

        if (dniNuevo != null) {
            clienteRepository.findByDni(dniNuevo).ifPresent(clienteExistente -> {
                if (!clienteExistente.getId().equals(id)) {
                    throw new BusinessException("Ya existe otro cliente registrado con este DNI");
                }
            });
        }

        cliente.setNombres(limpiarObligatorio(request.nombres()));
        cliente.setApellidos(limpiarObligatorio(request.apellidos()));
        cliente.setTelefono(limpiarObligatorio(request.telefono()));
        cliente.setDni(dniNuevo);
        cliente.setCorreo(limpiarOpcional(request.correo()));
        cliente.setDireccion(limpiarOpcional(request.direccion()));

        Cliente actualizado = clienteRepository.save(cliente);

        return convertirAResponse(actualizado);
    }

    public List<ClienteResponse> buscarClientes(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listarClientes();
        }

        String valor = texto.trim();

        return clienteRepository
                .findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(valor, valor)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public List<ClienteResponse> buscarPorTelefono(String telefono) {
        return clienteRepository.findByTelefono(telefono.trim())
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public ClienteResponse buscarPorDni(String dni) {
        Cliente cliente = clienteRepository.findByDni(dni.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró cliente con el DNI indicado"));

        return convertirAResponse(cliente);
    }

    public AdvertenciaDuplicadoResponse verificarDuplicados(String dni, String telefono) {
        ClienteResponse clientePorDni = null;
        List<ClienteResponse> clientesPorTelefono = List.of();

        String dniLimpio = limpiarOpcional(dni);
        String telefonoLimpio = limpiarOpcional(telefono);

        if (dniLimpio != null) {
            clientePorDni = clienteRepository.findByDni(dniLimpio)
                    .map(this::convertirAResponse)
                    .orElse(null);
        }

        if (telefonoLimpio != null) {
            clientesPorTelefono = clienteRepository.findByTelefono(telefonoLimpio)
                    .stream()
                    .map(this::convertirAResponse)
                    .toList();
        }

        boolean existeDni = clientePorDni != null;
        boolean existeTelefono = !clientesPorTelefono.isEmpty();

        String mensaje;

        if (existeDni && existeTelefono) {
            mensaje = "Se encontraron coincidencias por DNI y teléfono";
        } else if (existeDni) {
            mensaje = "Ya existe un cliente registrado con este DNI";
        } else if (existeTelefono) {
            mensaje = "Ya existe uno o más clientes registrados con este teléfono";
        } else {
            mensaje = "No se encontraron duplicados";
        }

        return new AdvertenciaDuplicadoResponse(
                existeDni,
                existeTelefono,
                clientePorDni,
                clientesPorTelefono,
                mensaje
        );
    }

    private Cliente buscarClientePorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
    }

    private ClienteResponse convertirAResponse(Cliente cliente) {
        String nombreCompleto = cliente.getNombres() + " " + cliente.getApellidos();

        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                nombreCompleto,
                cliente.getTelefono(),
                cliente.getDni(),
                cliente.getCorreo(),
                cliente.getDireccion(),
                cliente.getActivo(),
                cliente.getFechaCreacion()
        );
    }

    private String limpiarObligatorio(String valor) {
        return valor.trim();
    }

    private String limpiarOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }

        return valor.trim();
    }
}