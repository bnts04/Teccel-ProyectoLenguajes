package com.teccell.backend.service;

import com.teccell.backend.dto.CrearTecnicoRequest;
import com.teccell.backend.dto.RestablecerPasswordRequest;
import com.teccell.backend.dto.UsuarioResponse;
import com.teccell.backend.entity.Usuario;
import com.teccell.backend.enums.RolUsuario;
import com.teccell.backend.exception.BusinessException;
import com.teccell.backend.exception.ResourceNotFoundException;
import com.teccell.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioResponse crearTecnico(CrearTecnicoRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new BusinessException("El nombre de usuario ya se encuentra registrado");
        }

        Usuario tecnico = Usuario.builder()
                .nombreCompleto(request.nombreCompleto())
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .rol(RolUsuario.TECNICO)
                .activo(true)
                .build();

        Usuario guardado = usuarioRepository.save(tecnico);

        return convertirAResponse(guardado);
    }

    public List<UsuarioResponse> listarTecnicos() {
        return usuarioRepository.findByRol(RolUsuario.TECNICO)
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    public UsuarioResponse obtenerTecnico(Long id) {
        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario.getRol() != RolUsuario.TECNICO) {
            throw new BusinessException("El usuario solicitado no es un técnico");
        }

        return convertirAResponse(usuario);
    }

    public UsuarioResponse cambiarEstadoTecnico(Long id, boolean activo) {
        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario.getRol() != RolUsuario.TECNICO) {
            throw new BusinessException("Solo se puede activar o desactivar técnicos");
        }

        usuario.setActivo(activo);
        Usuario actualizado = usuarioRepository.save(usuario);

        return convertirAResponse(actualizado);
    }

    public UsuarioResponse restablecerPassword(Long id, RestablecerPasswordRequest request) {
        Usuario usuario = buscarUsuarioPorId(id);

        if (usuario.getRol() != RolUsuario.TECNICO) {
            throw new BusinessException("Solo se puede restablecer la contraseña de técnicos");
        }

        usuario.setPassword(passwordEncoder.encode(request.passwordNueva()));
        Usuario actualizado = usuarioRepository.save(usuario);

        return convertirAResponse(actualizado);
    }

    private Usuario buscarUsuarioPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
    }

    private UsuarioResponse convertirAResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }
}