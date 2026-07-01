package com.teccell.backend.config;

import com.teccell.backend.entity.Usuario;
import com.teccell.backend.enums.RolUsuario;
import com.teccell.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = Usuario.builder()
                    .nombreCompleto("Administrador General")
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .rol(RolUsuario.ADMIN)
                    .activo(true)
                    .build();

            usuarioRepository.save(admin);

            System.out.println("=============================================");
            System.out.println("ADMIN inicial creado");
            System.out.println("Usuario: admin");
            System.out.println("Contraseña: 123456");
            System.out.println("=============================================");
        }
    }
}