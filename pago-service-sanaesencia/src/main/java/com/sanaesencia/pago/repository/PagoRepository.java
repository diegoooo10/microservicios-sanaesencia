package com.sanaesencia.pago.repository;

import com.sanaesencia.pago.model.EstadoPago;
import com.sanaesencia.pago.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByToken(String token);
    Optional<Pago> findByOrdenCompra(String ordenCompra);

    List<Pago> findByReservaIdOrderByIdDesc(Long reservaId);

    boolean existsByReservaIdAndEstado(Long reservaId, EstadoPago estado);
}
