package com.teccell.backend.entity;

import com.teccell.backend.enums.EstadoOrden;
import com.teccell.backend.enums.TipoEventoHistorial;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenReparacion orden;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsable_id", nullable = false)
    private Usuario usuarioResponsable;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private TipoEventoHistorial tipoEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 40)
    private EstadoOrden estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", length = 40)
    private EstadoOrden estadoNuevo;

    @Column(nullable = false, length = 800)
    private String descripcion;

    @Column(name = "detalle_anterior", length = 800)
    private String detalleAnterior;

    @Column(name = "detalle_nuevo", length = 800)
    private String detalleNuevo;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @PrePersist
    public void prePersist() {
        if (this.fechaEvento == null) {
            this.fechaEvento = LocalDateTime.now();
        }
    }
}