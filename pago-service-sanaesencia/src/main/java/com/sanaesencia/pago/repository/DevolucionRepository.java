package com.sanaesencia.pago.repository;

import com.sanaesencia.pago.model.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DevolucionRepository extends JpaRepository<Devolucion, Long> {
    List<Devolucion> findByPagoId(Long pagoId);
    boolean existsByPagoId(Long pagoId);
}
