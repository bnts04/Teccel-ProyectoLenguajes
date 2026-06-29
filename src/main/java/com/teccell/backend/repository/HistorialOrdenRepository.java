package com.teccell.backend.repository;

import com.teccell.backend.entity.HistorialOrden;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialOrdenRepository extends JpaRepository<HistorialOrden, Long> {

    List<HistorialOrden> findByOrdenIdOrderByFechaEventoAsc(Long ordenId);
}