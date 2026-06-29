package com.teccell.backend.service;

import com.teccell.backend.dto.request.CambiarEstadoRequest;
import com.teccell.backend.dto.request.CrearOrdenRequest;
import com.teccell.backend.dto.request.RegistrarAvanceRequest;
import com.teccell.backend.dto.request.RegistrarDiagnosticoRequest;
import com.teccell.backend.dto.response.HistorialOrdenResponse;
import com.teccell.backend.dto.response.OrdenResponse;
import com.teccell.backend.entity.Cliente;
import com.teccell.backend.entity.Equipo;
import com.teccell.backend.entity.OrdenReparacion;
import com.teccell.backend.entity.Usuario;
import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.PrioridadOrden;
import com.teccell.backend.enums.RolUsuario;
import com.teccell.backend.enums.TipoAccesorio;
import com.teccell.backend.enums.TipoEventoHistorial;
import com.teccell.backend.exception.BusinessException;
import com.teccell.backend.exception.ResourceNotFoundException;
import com.teccell.backend.repository.EquipoRepository;
import com.teccell.backend.repository.OrdenReparacionRepository;
import com.teccell.backend.repository.UsuarioRepository;
import com.teccell.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdenService {

    private final OrdenReparacionRepository ordenRepository;
    private final EquipoRepository equipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistorialService historialService;

    private final SecureRandom random = new SecureRandom();

    @Transactional
    public OrdenResponse crearOrden(CrearOrdenRequest request) {
        Usuario usuarioActual = obtenerUsuarioActual();

        Equipo equipo = equipoRepository.findById(request.equipoId())
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + request.equipoId()));

        Usuario tecnicoResponsable = determinarTecnicoResponsable(usuarioActual, request.tecnicoResponsableId());

        validarAccesorios(request.accesoriosEntregados(), request.otrosAccesorios());

        Set<TipoAccesorio> accesorios = prepararAccesorios(request.accesoriosEntregados());

        PrioridadOrden prioridad = request.prioridad() != null
                ? request.prioridad()
                : PrioridadOrden.NORMAL;

        LocalDateTime fechaIngreso = LocalDateTime.now();
        LocalDate fechaEstimada = LocalDate.now().plusDays(request.diasEstimados());

        OrdenReparacion orden = OrdenReparacion.builder()
                .ticket(generarTicket())
                .equipo(equipo)
                .tecnicoResponsable(tecnicoResponsable)
                .fallaReportada(limpiarObligatorio(request.fallaReportada()))
                .estadoFisicoRecepcion(limpiarOpcional(request.estadoFisicoRecepcion()))
                .accesoriosEntregados(accesorios)
                .otrosAccesorios(prepararOtrosAccesorios(accesorios, request.otrosAccesorios()))
                .precioAcordado(request.precioAcordado())
                .diasEstimados(request.diasEstimados())
                .fechaIngreso(fechaIngreso)
                .fechaEstimadaEntrega(fechaEstimada)
                .prioridad(prioridad)
                .estado(EstadoOrden.RECIBIDO)
                .activo(true)
                .build();

        OrdenReparacion guardada = ordenRepository.save(orden);

        historialService.registrarEvento(
                guardada,
                usuarioActual,
                TipoEventoHistorial.CREACION,
                null,
                EstadoOrden.RECIBIDO,
                "Orden creada con ticket " + guardada.getTicket(),
                null,
                "Falla reportada: " + guardada.getFallaReportada()
        );

        return convertirAResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarOrdenes() {
        Usuario usuarioActual = obtenerUsuarioActual();

        if (usuarioActual.getRol() == RolUsuario.ADMIN) {
            return ordenRepository.findAll()
                    .stream()
                    .map(this::convertirAResponse)
                    .toList();
        }

        return ordenRepository.findByTecnicoResponsableId(usuarioActual.getId())
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrdenResponse obtenerOrden(Long id) {
        Usuario usuarioActual = obtenerUsuarioActual();

        OrdenReparacion orden = buscarOrdenPorId(id);

        validarAccesoAOrden(usuarioActual, orden);

        return convertirAResponse(orden);
    }

    @Transactional(readOnly = true)
    public OrdenResponse buscarPorTicketInterno(String ticket) {
        Usuario usuarioActual = obtenerUsuarioActual();

        OrdenReparacion orden = ordenRepository.findByTicket(ticket.trim())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró orden con el ticket indicado"));

        validarAccesoAOrden(usuarioActual, orden);

        return convertirAResponse(orden);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> listarOrdenesPorEquipo(Long equipoId) {
        Usuario usuarioActual = obtenerUsuarioActual();

        equipoRepository.findById(equipoId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipo no encontrado con ID: " + equipoId));

        return ordenRepository.findByEquipoId(equipoId)
                .stream()
                .filter(orden -> usuarioActual.getRol() == RolUsuario.ADMIN
                        || orden.getTecnicoResponsable().getId().equals(usuarioActual.getId()))
                .map(this::convertirAResponse)
                .toList();
    }

    @Transactional
    public OrdenResponse cambiarEstado(Long id, CambiarEstadoRequest request) {
        Usuario usuarioActual = obtenerUsuarioActual();

        OrdenReparacion orden = buscarOrdenPorId(id);

        validarAccesoAOrden(usuarioActual, orden);
        validarOrdenAbierta(orden);

        EstadoOrden estadoAnterior = orden.getEstado();
        EstadoOrden estadoNuevo = request.nuevoEstado();

        validarTransicionEstado(orden, estadoNuevo, request.motivo());

        orden.setEstado(estadoNuevo);

        OrdenReparacion actualizada = ordenRepository.save(orden);

        String motivo = limpiarOpcional(request.motivo());

        String descripcion = "Cambio de estado de " + estadoAnterior.name() + " a " + estadoNuevo.name();

        if (motivo != null) {
            descripcion += ". Motivo: " + motivo;
        }

        historialService.registrarEvento(
                actualizada,
                usuarioActual,
                TipoEventoHistorial.CAMBIO_ESTADO,
                estadoAnterior,
                estadoNuevo,
                descripcion,
                estadoAnterior.name(),
                estadoNuevo.name()
        );

        return convertirAResponse(actualizada);
    }

    @Transactional
    public OrdenResponse registrarDiagnostico(Long id, RegistrarDiagnosticoRequest request) {
        Usuario usuarioActual = obtenerUsuarioActual();

        OrdenReparacion orden = buscarOrdenPorId(id);

        validarAccesoAOrden(usuarioActual, orden);
        validarOrdenAbierta(orden);

        if (orden.getEstado() == EstadoOrden.RECIBIDO) {
            throw new BusinessException("Primero debe cambiar la orden al estado EN_DIAGNOSTICO");
        }

        String diagnosticoAnterior = orden.getDiagnostico();

        orden.setDiagnostico(limpiarObligatorio(request.diagnostico()));
        orden.setFechaDiagnostico(LocalDateTime.now());

        OrdenReparacion actualizada = ordenRepository.save(orden);

        historialService.registrarEvento(
                actualizada,
                usuarioActual,
                TipoEventoHistorial.DIAGNOSTICO,
                null,
                null,
                "Diagnóstico registrado",
                diagnosticoAnterior,
                actualizada.getDiagnostico()
        );

        String observacion = limpiarOpcional(request.observacion());

        if (observacion != null) {
            historialService.registrarEvento(
                    actualizada,
                    usuarioActual,
                    TipoEventoHistorial.AVANCE,
                    null,
                    null,
                    "Observación del diagnóstico: " + observacion,
                    null,
                    observacion
            );
        }

        return convertirAResponse(actualizada);
    }

    @Transactional
    public HistorialOrdenResponse registrarAvance(Long id, RegistrarAvanceRequest request) {
        Usuario usuarioActual = obtenerUsuarioActual();

        OrdenReparacion orden = buscarOrdenPorId(id);

        validarAccesoAOrden(usuarioActual, orden);
        validarOrdenAbierta(orden);

        if (orden.getEstado() == EstadoOrden.RECIBIDO) {
            throw new BusinessException("No se puede registrar avance si la orden aún está en estado RECIBIDO");
        }

        String descripcionAvance = limpiarObligatorio(request.descripcion());

        historialService.registrarEvento(
                orden,
                usuarioActual,
                TipoEventoHistorial.AVANCE,
                null,
                null,
                "Avance registrado: " + descripcionAvance,
                null,
                descripcionAvance
        );

        List<HistorialOrdenResponse> historial = historialService.listarHistorialPorOrden(id);

        return historial.get(historial.size() - 1);
    }

    @Transactional(readOnly = true)
    public List<HistorialOrdenResponse> listarHistorial(Long id) {
        Usuario usuarioActual = obtenerUsuarioActual();

        OrdenReparacion orden = buscarOrdenPorId(id);

        validarAccesoAOrden(usuarioActual, orden);

        return historialService.listarHistorialPorOrden(id);
    }

    private OrdenReparacion buscarOrdenPorId(Long id) {
        return ordenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
    }

    private Usuario determinarTecnicoResponsable(Usuario usuarioActual, Long tecnicoResponsableId) {
        if (usuarioActual.getRol() == RolUsuario.TECNICO) {
            return usuarioActual;
        }

        if (tecnicoResponsableId == null) {
            throw new BusinessException("El ADMIN debe seleccionar un técnico responsable");
        }

        Usuario tecnico = usuarioRepository.findById(tecnicoResponsableId)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado con ID: " + tecnicoResponsableId));

        if (tecnico.getRol() != RolUsuario.TECNICO) {
            throw new BusinessException("El usuario seleccionado no tiene rol TÉCNICO");
        }

        if (!Boolean.TRUE.equals(tecnico.getActivo())) {
            throw new BusinessException("El técnico seleccionado se encuentra inactivo");
        }

        return tecnico;
    }

    private void validarAccesoAOrden(Usuario usuarioActual, OrdenReparacion orden) {
        if (usuarioActual.getRol() == RolUsuario.ADMIN) {
            return;
        }

        if (!orden.getTecnicoResponsable().getId().equals(usuarioActual.getId())) {
            throw new BusinessException("No tiene permiso para consultar esta orden");
        }
    }

    private void validarOrdenAbierta(OrdenReparacion orden) {
        if (orden.getEstado() == EstadoOrden.ENTREGADO || orden.getEstado() == EstadoOrden.CANCELADO) {
            throw new BusinessException("La orden ya se encuentra cerrada y no puede modificarse");
        }
    }

    private void validarTransicionEstado(OrdenReparacion orden, EstadoOrden estadoNuevo, String motivo) {
        EstadoOrden estadoActual = orden.getEstado();

        if (estadoNuevo == EstadoOrden.ENTREGADO) {
            throw new BusinessException("La entrega formal se realizará en la Fase 8");
        }

        if (estadoNuevo == EstadoOrden.CANCELADO) {
            throw new BusinessException("La cancelación formal se realizará en la Fase 8");
        }

        if (estadoActual == estadoNuevo) {
            throw new BusinessException("La orden ya se encuentra en el estado seleccionado");
        }

        boolean transicionNormal =
                estadoActual == EstadoOrden.RECIBIDO && estadoNuevo == EstadoOrden.EN_DIAGNOSTICO
                        || estadoActual == EstadoOrden.EN_DIAGNOSTICO && estadoNuevo == EstadoOrden.EN_REPARACION
                        || estadoActual == EstadoOrden.EN_REPARACION && estadoNuevo == EstadoOrden.LISTO_PARA_RECOGER;

        boolean retroceso =
                estadoActual == EstadoOrden.EN_DIAGNOSTICO && estadoNuevo == EstadoOrden.RECIBIDO
                        || estadoActual == EstadoOrden.EN_REPARACION && estadoNuevo == EstadoOrden.EN_DIAGNOSTICO
                        || estadoActual == EstadoOrden.LISTO_PARA_RECOGER && estadoNuevo == EstadoOrden.EN_REPARACION;

        if (estadoNuevo == EstadoOrden.EN_REPARACION && limpiarOpcional(orden.getDiagnostico()) == null) {
            throw new BusinessException("Para pasar a EN_REPARACION debe registrar primero un diagnóstico");
        }

        if (retroceso && limpiarOpcional(motivo) == null) {
            throw new BusinessException("Para retroceder de estado debe registrar un motivo");
        }

        if (!transicionNormal && !retroceso) {
            throw new BusinessException("Transición de estado no permitida: " + estadoActual + " → " + estadoNuevo);
        }
    }

    private void validarAccesorios(Set<TipoAccesorio> accesorios, String otrosAccesorios) {
        if (accesorios == null || accesorios.isEmpty()) {
            return;
        }

        if (accesorios.contains(TipoAccesorio.NINGUNO) && accesorios.size() > 1) {
            throw new BusinessException("Si selecciona NINGUNO, no debe seleccionar otros accesorios");
        }

        if (accesorios.contains(TipoAccesorio.OTROS) && limpiarOpcional(otrosAccesorios) == null) {
            throw new BusinessException("Debe describir los accesorios cuando selecciona OTROS");
        }
    }

    private Set<TipoAccesorio> prepararAccesorios(Set<TipoAccesorio> accesorios) {
        if (accesorios == null || accesorios.isEmpty()) {
            return new HashSet<>(Set.of(TipoAccesorio.NINGUNO));
        }

        return new HashSet<>(accesorios);
    }

    private String prepararOtrosAccesorios(Set<TipoAccesorio> accesorios, String otrosAccesorios) {
        if (accesorios.contains(TipoAccesorio.OTROS)) {
            return limpiarOpcional(otrosAccesorios);
        }

        return null;
    }

    private String generarTicket() {
        String ticket;

        do {
            int numero = 100000 + random.nextInt(900000);
            ticket = "TC-" + numero;
        } while (ordenRepository.existsByTicket(ticket));

        return ticket;
    }

    private Usuario obtenerUsuarioActual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("No existe un usuario autenticado");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof CustomUserDetails userDetails)) {
            throw new BusinessException("No se pudo obtener el usuario autenticado");
        }

        return usuarioRepository.findById(userDetails.getId())
                .orElseThrow(() -> new BusinessException("El usuario autenticado ya no existe"));
    }

    private OrdenResponse convertirAResponse(OrdenReparacion orden) {
        Equipo equipo = orden.getEquipo();
        Cliente cliente = equipo.getCliente();
        Usuario tecnico = orden.getTecnicoResponsable();

        String nombreCliente = cliente.getNombres() + " " + cliente.getApellidos();

        Set<String> accesorios = orden.getAccesoriosEntregados()
                .stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new OrdenResponse(
                orden.getId(),
                orden.getTicket(),

                equipo.getId(),
                equipo.getCodigoInterno(),
                equipo.getTipo().name(),
                equipo.getMarca(),
                equipo.getModelo(),
                equipo.getColor(),

                cliente.getId(),
                nombreCliente,
                cliente.getTelefono(),

                tecnico.getId(),
                tecnico.getNombreCompleto(),

                orden.getFallaReportada(),
                orden.getDiagnostico(),
                orden.getEstadoFisicoRecepcion(),
                accesorios,
                orden.getOtrosAccesorios(),

                orden.getPrecioAcordado(),
                orden.getDiasEstimados(),
                orden.getFechaIngreso(),
                orden.getFechaEstimadaEntrega(),

                orden.getPrioridad().name(),
                orden.getEstado().name(),
                orden.getActivo(),
                orden.getFechaCreacion()
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