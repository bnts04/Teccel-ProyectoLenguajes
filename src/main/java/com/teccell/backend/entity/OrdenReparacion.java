package com.teccell.backend.entity;

import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.PrioridadOrden;
import com.teccell.backend.enums.TipoAccesorio;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ordenes_reparacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdenReparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnicoResponsable;

    @Column(name = "falla_reportada", nullable = false, length = 500)
    private String fallaReportada;

    @Column(name = "diagnostico", length = 800)
    private String diagnostico;

    @Column(name = "fecha_diagnostico")
    private LocalDateTime fechaDiagnostico;

    @Column(name = "estado_fisico_recepcion", length = 300)
    private String estadoFisicoRecepcion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "orden_accesorios",
            joinColumns = @JoinColumn(name = "orden_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "accesorio", length = 30)
    @Builder.Default
    private Set<TipoAccesorio> accesoriosEntregados = new HashSet<>();

    @Column(name = "otros_accesorios", length = 200)
    private String otrosAccesorios;

    @Column(name = "precio_acordado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAcordado;

    @Column(name = "dias_estimados", nullable = false)
    private Integer diasEstimados;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "fecha_estimada_entrega", nullable = false)
    private LocalDate fechaEstimadaEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PrioridadOrden prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoOrden estado;

    @Column(nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();

        if (this.fechaIngreso == null) {
            this.fechaIngreso = LocalDateTime.now();
        }

        if (this.estado == null) {
            this.estado = EstadoOrden.RECIBIDO;
        }

        if (this.prioridad == null) {
            this.prioridad = PrioridadOrden.NORMAL;
        }

        if (this.activo == null) {
            this.activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}