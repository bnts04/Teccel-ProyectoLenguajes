package com.teccell.backend.entity;

import com.teccell.backend.enums.TipoEquipo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "equipos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_interno", nullable = false, unique = true, length = 20)
    private String codigoInterno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEquipo tipo;

    @Column(name = "descripcion_tipo_otro", length = 80)
    private String descripcionTipoOtro;

    @Column(nullable = false, length = 80)
    private String marca;

    @Column(nullable = false, length = 80)
    private String modelo;

    @Column(length = 50)
    private String color;

    @Column(name = "caracteristicas_fisicas", length = 300)
    private String caracteristicasFisicas;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.activo == null) {
            this.activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}