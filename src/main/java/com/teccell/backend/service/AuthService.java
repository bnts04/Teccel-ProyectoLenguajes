package com.teccell.backend.service;

import com.teccell.backend.dto.CambiarPasswordRequest;
import com.teccell.backend.dto.LoginRequest;
import com.teccell.backend.dto.LoginResponse;
import com.teccell.backend.dto.UsuarioResponse;
import com.teccell.backend.entity.Usuario;
import com.teccell.backend.exception.BusinessException;
import com.teccell.backend.repository.UsuarioRepository;
import com.teccell.backend.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return new LoginResponse(
                userDetails.getId(),
                userDetails.getNombreCompleto(),
                userDetails.getUsername(),
                userDetails.getRol(),
                "Login correcto"
        );
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();
    }

    public UsuarioResponse obtenerUsuarioActual() {
        Usuario usuario = obtenerEntidadUsuarioActual();

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombreCompleto(),
                usuario.getUsername(),
                usuario.getRol().name(),
                usuario.getActivo(),
                usuario.getFechaCreacion()
        );
    }

    public UsuarioResponse cambiarPassword(CambiarPasswordRequest request) {
        Usuario usuario = obtenerEntidadUsuarioActual();

        if (!passwordEncoder.matches(request.passwordActual(), usuario.getPassword())) {
            throw new BusinessException("La contraseña actual no es correcta");
        }

        usuario.setPassword(passwordEncoder.encode(request.passwordNueva()));
        Usuario actualizado = usuarioRepository.save(usuario);

        return new UsuarioResponse(
                actualizado.getId(),
                actualizado.getNombreCompleto(),
                actualizado.getUsername(),
                actualizado.getRol().name(),
                actualizado.getActivo(),
                actualizado.getFechaCreacion()
        );
    }

    private Usuario obtenerEntidadUsuarioActual() {
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
}