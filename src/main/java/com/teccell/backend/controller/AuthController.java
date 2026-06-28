package com.teccell.backend.controller;

import com.teccell.backend.dto.CambiarPasswordRequest;
import com.teccell.backend.dto.LoginRequest;
import com.teccell.backend.dto.LoginResponse;
import com.teccell.backend.dto.UsuarioResponse;
import com.teccell.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return authService.login(request, httpRequest);
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        authService.logout(request);
        return "Sesión cerrada correctamente";
    }

    @GetMapping("/me")
    public UsuarioResponse me() {
        return authService.obtenerUsuarioActual();
    }

    @PutMapping("/cambiar-password")
    public UsuarioResponse cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        return authService.cambiarPassword(request);
    }
}