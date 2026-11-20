package com.sanaesencia.pago.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "devoluciones", indexes = {
        @Index(name = "idx_devolucion_pago", columnList = "pago_id")
})
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pago_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_devolucion_pago"))
    private Pago pago;

    @Column(nullable = false)
    private Integer monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EstadoDevolucion estado = EstadoDevolucion.PENDIENTE;

    @Column(length = 100)
    private String tipo;

    @Column(length = 250)
    private String detalle;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    public Long getId() { return id; }
    public Pago getPago() { return pago; }
    public void setPago(Pago pago) { this.pago = pago; }
    public Integer getMonto() { return monto; }
    public void setMonto(Integer monto) { this.monto = monto; }
    public EstadoDevolucion getEstado() { return estado; }
    public void setEstado(EstadoDevolucion estado) { this.estado = estado; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public Instant getCreadoEn() { return creadoEn; }
}
