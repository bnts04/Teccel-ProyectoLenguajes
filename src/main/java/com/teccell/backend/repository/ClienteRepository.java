package com.teccell.backend.repository;

import com.teccell.backend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);

    boolean existsByDni(String dni);

    List<Cliente> findByTelefono(String telefono);

    List<Cliente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(
            String nombres,
            String apellidos
    );
}