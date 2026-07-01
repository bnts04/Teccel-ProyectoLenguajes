package com.teccell.backend.service;

import com.teccell.backend.dto.request.ActualizarEquipoRequest;
import com.teccell.backend.dto.request.CrearEquipoRequest;
import com.teccell.backend.dto.response.EquipoResponse;
import com.teccell.backend.entity.Cliente;
import com.teccell.backend.entity.Equipo;
import com.teccell.backend.enums.TipoEquipo;
import com.teccell.backend.exception.BusinessException;
import com.teccell.backend.exception.ResourceNotFoundException;
import com.teccell.backend.repository.ClienteRepository;
import com.teccell.backend.repository.EquipoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipoService {

    private final EquipoRepository equipoRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public EquipoResponse crearEquipo(CrearEquipoRequest request) {
        Cliente cliente = buscarClientePorId(request.clienteId());

        validarTipoOtro(request.tipo(), request.descripcionTipoOtro());

        Equipo equipo = Equipo.builder()
                .codigoInterno(generarCodigoInterno())
                .cliente(cliente)
                .tipo(request.tipo())
                .descripcionTipoOtro(limpiarOpcional(request.descripcionTipoOtro()))
                .marca(limpiarObligatorio(request.marca()))
                .modelo(limpiarObligatorio(request.modelo()))
                .color(limpiarOpcional(request.color()))
                .caracteristicasFisicas(limpiarOpcional(request.caracteristicasFisicas()))
                .activo(true)
                .build();

        Equipo guardado = equipoRepository.save(equipo);

        return convertirAResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listarEquipos() {
        return equipoRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipoResponse obtenerEquipo(Long id) {
        Equipo equipo = buscarEquipoPorId(id);
        return convertirAResponse(equipo);
    }

    @Transactional(readOnly = true)
    public EquipoResponse buscarPorCodigoInterno(String codigoInterno) {
        Equipo equipo = equipoRepository.findByCodigoInterno(codigoInterno.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró equipo con el código indicado"));

        return convertirAResponse(equipo);
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listarEquiposPorCliente(Long clienteId) {
        buscarClientePorId(clienteId);

        return equipoRepository.findByClienteId(clienteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EquipoResponse> listarEquiposActivosPorCliente(Long clienteId) {
        buscarClientePorId(clienteId);

        return equipoRepository.findByClienteIdAndActivoTrue(clienteId)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public EquipoResponse actualizarEquipo(Long id, ActualizarEquipoRequest request) {
        Equipo equipo = buscarEquipoPorId(id);

        validarTipoOtro(request.tipo(), request.descripcionTipoOtro());

        equipo.setTipo(request.tipo());
        equipo.setDescripcionTipoOtro(limpiarOpcional(request.descripcionTipoOtro()));
        equipo.setMarca(limpiarObligatorio(request.marca()));
        equipo.setModelo(limpiarObligatorio(request.modelo()));
        equipo.setColor(limpiarOpcional(request.color()));
        equipo.setCaracteristicasFisicas(limpiarOpcional(request.caracteristicasFisicas()));

        if (request.activo() != null) {
            equipo.setActivo(request.activo());
        }

        Equipo actualizado = equipoRepository.save(equipo);

        return convertirAResponse(actualizado);
    }

    private Cliente buscarClientePorId(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));
    }

    private Equipo buscarEquipoPorId(Long id) {
        return equipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + id));
    }

    private void validarTipoOtro(TipoEquipo tipo, String descripcionTipoOtro) {
        if (tipo == TipoEquipo.OTRO && limpiarOpcional(descripcionTipoOtro) == null) {
            throw new BusinessException("Debe especificar la descripción cuando el tipo de equipo es OTRO");
        }
    }

    private String generarCodigoInterno() {
        long siguienteNumero = equipoRepository.count() + 1;
        String codigo = String.format("EQ-%06d", siguienteNumero);

        while (equipoRepository.existsByCodigoInterno(codigo)) {
            siguienteNumero++;
            codigo = String.format("EQ-%06d", siguienteNumero);
        }

        return codigo;
    }

    private EquipoResponse convertirAResponse(Equipo equipo) {
        Cliente cliente = equipo.getCliente();

        String nombreCliente = cliente.getNombres() + " " + cliente.getApellidos();

        return new EquipoResponse(
                equipo.getId(),
                equipo.getCodigoInterno(),
                cliente.getId(),
                nombreCliente,
                equipo.getTipo().name(),
                equipo.getDescripcionTipoOtro(),
                equipo.getMarca(),
                equipo.getModelo(),
                equipo.getColor(),
                equipo.getCaracteristicasFisicas(),
                equipo.getActivo(),
                equipo.getFechaCreacion()
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