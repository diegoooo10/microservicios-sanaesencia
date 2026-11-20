package com.sanaesencia.pago.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pagos", indexes = {
        @Index(name = "idx_pago_reserva", columnList = "reserva_id")
})
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private Integer monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private EstadoPago estado = EstadoPago.PENDIENTE;

    @Column(unique = true, length = 64)
    private String token;

    @Column(name = "orden_compra", nullable = false, unique = true, length = 26)
    private String ordenCompra;

    @Column(name = "codigo_autorizacion", length = 100)
    private String codigoAutorizacion;

    @Column(name = "reserva_confirmada", nullable = false)
    private boolean reservaConfirmada = false;

    @OneToMany(mappedBy = "pago")
    private List<Devolucion> devoluciones = new ArrayList<>();

    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn = Instant.now();

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn = Instant.now();

    @Version
    private Long version;

    @PreUpdate
    void actualizarFecha() {
        actualizadoEn = Instant.now();
    }

    public Long getId() { return id; }
    public Long getReservaId() { return reservaId; }
    public void setReservaId(Long reservaId) { this.reservaId = reservaId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public Integer getMonto() { return monto; }
    public void setMonto(Integer monto) { this.monto = monto; }
    public EstadoPago getEstado() { return estado; }
    public void setEstado(EstadoPago estado) { this.estado = estado; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getOrdenCompra() { return ordenCompra; }
    public void setOrdenCompra(String ordenCompra) { this.ordenCompra = ordenCompra; }
    public String getCodigoAutorizacion() { return codigoAutorizacion; }
    public void setCodigoAutorizacion(String codigoAutorizacion) { this.codigoAutorizacion = codigoAutorizacion; }
    public List<Devolucion> getDevoluciones() { return devoluciones; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public boolean isReservaConfirmada() {
    return reservaConfirmada;
}

public void setReservaConfirmada(boolean reservaConfirmada) {
    this.reservaConfirmada = reservaConfirmada;
}
}
