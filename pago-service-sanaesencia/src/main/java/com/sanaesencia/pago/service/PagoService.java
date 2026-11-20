package com.sanaesencia.pago.service;

import com.sanaesencia.pago.model.Devolucion;
import com.sanaesencia.pago.model.EstadoDevolucion;
import com.sanaesencia.pago.model.EstadoPago;
import com.sanaesencia.pago.model.Pago;
import com.sanaesencia.pago.model.PagoDtos.CrearPago;
import com.sanaesencia.pago.model.PagoDtos.DevolucionResponse;
import com.sanaesencia.pago.model.PagoDtos.InicioResponse;
import com.sanaesencia.pago.model.PagoDtos.PagoResponse;
import com.sanaesencia.pago.repository.DevolucionRepository;
import com.sanaesencia.pago.repository.PagoRepository;
import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.sanaesencia.pago.client.ReservaClient;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PagoService {

    private static final int MONTO_SERVICIO = 25_000;

    private final PagoRepository pagos;
    private final DevolucionRepository devoluciones;
    private final ReservaClient reservaClient;

    @Value("${app.webpay.return-url}")
    private String returnUrl;

    public PagoService(PagoRepository pagos, DevolucionRepository devoluciones, ReservaClient reservaClient) {
        this.pagos = pagos;
        this.devoluciones = devoluciones;
        this.reservaClient = reservaClient;
    }

    private WebpayPlus.Transaction transaccion() {
        return new WebpayPlus.Transaction(new WebpayOptions(
                IntegrationCommerceCodes.WEBPAY_PLUS,
                IntegrationApiKeys.WEBPAY,
                IntegrationType.TEST));
    }

    public PagoResponse dto(Pago pago) {
        return new PagoResponse(pago.getId(), pago.getReservaId(), pago.getUsuarioId(),
                pago.getMonto(), pago.getEstado(), pago.getOrdenCompra());
    }

    public Pago obtener(Long id) {
        return pagos.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));
    }

    public List<PagoResponse> listar(Long reservaId) {
        return pagos.findByReservaIdOrderByIdDesc(reservaId).stream().map(this::dto).toList();
    }


    private void sincronizarReserva(Pago pago) {

        if (pago.getEstado() != EstadoPago.APROBADO
                || pago.isReservaConfirmada()) {
            return;
        }

        try {
            reservaClient.confirmarPago(pago.getReservaId());

            // Recuperar la versión actualizada desde MySQL.
            Pago pagoActualizado = pagos.findById(pago.getId())
                    .orElseThrow(() ->
                            new NoSuchElementException("Pago no encontrado")
                    );

            if (!pagoActualizado.isReservaConfirmada()) {
                pagoActualizado.setReservaConfirmada(true);
                pagos.saveAndFlush(pagoActualizado);
            }

        } catch (org.springframework.web.client.RestClientException ex) {

            org.slf4j.LoggerFactory.getLogger(PagoService.class)
                    .error(
                            "No se pudo confirmar la reserva {} del pago {}",
                            pago.getReservaId(),
                            pago.getId(),
                            ex
                    );
        }
    }


    // SOLO invocar desde reserva-service, despues de validar el propietario y el vencimiento.
    public InicioResponse crear(CrearPago request) throws Exception {
        if (pagos.existsByReservaIdAndEstado(request.reservaId(), EstadoPago.APROBADO)) {
            throw new IllegalStateException("La reserva ya tiene un pago aprobado");
        }
        if (pagos.findByReservaIdOrderByIdDesc(request.reservaId()).stream()
                .anyMatch(p -> p.getEstado() == EstadoPago.PENDIENTE)) {
            throw new IllegalStateException("La reserva ya tiene una transacción pendiente");
        }

        Pago pago = new Pago();
        pago.setReservaId(request.reservaId());
        pago.setUsuarioId(request.usuarioId());
        pago.setMonto(MONTO_SERVICIO);
        pago.setOrdenCompra("SE" + UUID.randomUUID().toString().replace("-", "").substring(0, 20));
        pagos.saveAndFlush(pago);

        try {
            var resultado = transaccion().create(pago.getOrdenCompra(),
                    "reserva-" + pago.getReservaId(), pago.getMonto(), returnUrl);
            pago.setToken(resultado.getToken());
            pagos.save(pago);
            return new InicioResponse(dto(pago), resultado.getUrl(), resultado.getToken());
        } catch (Exception ex) {
            pago.setEstado(EstadoPago.REVISION_MANUAL);
            pagos.save(pago);
            throw ex;
        }
    }


public PagoResponse confirmar(String token) throws Exception {

    Pago pago = pagos.findByToken(token)
            .orElseThrow(() ->
                    new NoSuchElementException("Token de pago desconocido")
            );

    // Si Transbank ya aprobó el pago, no volvemos a ejecutar commit.
    if (pago.getEstado() == EstadoPago.APROBADO) {
        sincronizarReserva(pago);
        return dto(pago);
    }

    if (pago.getEstado() != EstadoPago.PENDIENTE) {
        throw new IllegalStateException("El pago ya fue procesado");
    }

    var resultado = transaccion().commit(token);

    if (!pago.getOrdenCompra().equals(resultado.getBuyOrder())
            || Double.compare(
                    resultado.getAmount(),
                    pago.getMonto().doubleValue()
            ) != 0) {

        pago.setEstado(EstadoPago.REVISION_MANUAL);
        pagos.saveAndFlush(pago);

        throw new IllegalStateException(
                "Datos de transacción inconsistentes; requiere revisión"
        );
    }

    boolean aprobado = "AUTHORIZED".equals(resultado.getStatus())
            && resultado.getResponseCode() == 0;

    pago.setEstado(
            aprobado ? EstadoPago.APROBADO : EstadoPago.RECHAZADO
    );

    pago.setCodigoAutorizacion(resultado.getAuthorizationCode());

    // Guardamos el resultado de Transbank antes de contactar
    // a reserva-service.
    pagos.saveAndFlush(pago);

    if (aprobado) {
        sincronizarReserva(pago);
    }

    return dto(pago);
}


    // Nunca marcar como DEVUELTO solo porque la solicitud fue aceptada.
    // La conciliación posterior debe determinar el estado definitivo.
    public DevolucionResponse devolver(Long pagoId) throws Exception {
        Pago pago = obtener(pagoId);
        if (pago.getEstado() != EstadoPago.APROBADO) {
            throw new IllegalStateException("Solo se pueden devolver pagos aprobados");
        }
        if (devoluciones.existsByPagoId(pagoId)) {
            throw new IllegalStateException("Ya existe una solicitud de devolución");
        }

        Devolucion devolucion = new Devolucion();
        devolucion.setPago(pago);
        devolucion.setMonto(pago.getMonto());
        devolucion.setEstado(EstadoDevolucion.PENDIENTE);
        devoluciones.saveAndFlush(devolucion);
        pago.setEstado(EstadoPago.DEVOLUCION_PENDIENTE);
        pagos.saveAndFlush(pago);

        try {
            var respuesta = transaccion().refund(pago.getToken(), pago.getMonto().doubleValue());
            devolucion.setTipo(respuesta.getType());
            devolucion.setEstado(EstadoDevolucion.SOLICITADA);
            devolucion.setDetalle("Transbank recibió la solicitud; verificar resultado final");
        } catch (Exception ex) {
            devolucion.setEstado(EstadoDevolucion.REVISION_MANUAL);
            devolucion.setDetalle("Resultado incierto: verificar en Transbank antes de reintentar");
            pago.setEstado(EstadoPago.REVISION_MANUAL);
        }

        devoluciones.save(devolucion);
        pagos.save(pago);
        return new DevolucionResponse(devolucion.getId(), pago.getId(),
                devolucion.getMonto(), devolucion.getEstado(), devolucion.getTipo());
    }

    public List<DevolucionResponse> devoluciones(Long pagoId) {
        obtener(pagoId);
        return devoluciones.findByPagoId(pagoId).stream()
                .map(d -> new DevolucionResponse(d.getId(), pagoId, d.getMonto(),
                        d.getEstado(), d.getTipo()))
                .toList();
    }
}
