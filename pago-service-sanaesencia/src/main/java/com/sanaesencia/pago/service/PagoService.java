
package com.sanaesencia.pago.service;

import cl.transbank.common.IntegrationApiKeys;
import cl.transbank.common.IntegrationCommerceCodes;
import cl.transbank.common.IntegrationType;
import cl.transbank.webpay.common.WebpayOptions;
import cl.transbank.webpay.webpayplus.WebpayPlus;

import com.sanaesencia.pago.client.ReservaClient;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class PagoService {

    private static final Logger log = LoggerFactory.getLogger(PagoService.class);
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

    // Configura Webpay Plus en ambiente de pruebas.
    private WebpayPlus.Transaction transaccion() {
        return new WebpayPlus.Transaction(new WebpayOptions(
                IntegrationCommerceCodes.WEBPAY_PLUS,
                IntegrationApiKeys.WEBPAY,
                IntegrationType.TEST
        ));
    }

    // Convierte una entidad Pago en un DTO de respuesta.
    public PagoResponse dto(Pago pago) {
        return new PagoResponse(pago.getId(), pago.getReservaId(), pago.getUsuarioId(),
                pago.getMonto(), pago.getEstado(), pago.getOrdenCompra());
    }

    // Obtiene un pago mediante su identificador.
    public Pago obtener(Long id) {
        return pagos.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));
    }

    // Lista los pagos de una reserva desde el más reciente.
    public List<PagoResponse> listar(Long reservaId) {
        return pagos.findByReservaIdOrderByIdDesc(reservaId).stream()
                .map(this::dto)
                .toList();
    }

    // Consulta el estado de una transacción en Transbank y valida sus datos.
    public String consultarEstadoWebpay(String token) throws Exception {
        Pago pago = pagos.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Token de pago desconocido"));

        var resultado = transaccion().status(token);

        if (!pago.getOrdenCompra().equals(resultado.getBuyOrder())
                || Double.compare(resultado.getAmount(), pago.getMonto().doubleValue()) != 0) {
            throw new IllegalStateException("Datos de transacción inconsistentes");
        }

        return resultado.getStatus();
    }



    // Valida el retorno interrumpido y procesa el resultado del pago.
    public PagoResponse validarRetornoInterrumpido(String ordenCompra, String idSesion) throws Exception {
        Pago pago = pagos.findByOrdenCompra(ordenCompra)
                .orElseThrow(() -> new NoSuchElementException("Orden de compra desconocida"));

        if (!("reserva-" + pago.getReservaId()).equals(idSesion)) {
            throw new IllegalStateException("La sesión no corresponde a la reserva");
        }

        if (pago.getToken() == null || pago.getToken().isBlank()) {
            throw new IllegalStateException("El pago no tiene un token válido");
        }

        return anularPago(pago.getToken());
    }



    // Confirma una reserva pagada y registra su sincronización con reserva-service.
    private void sincronizarReserva(Pago pago) {
        if (pago.getEstado() != EstadoPago.APROBADO || pago.isReservaConfirmada()) {
            return;
        }

        try {
            reservaClient.confirmarPago(pago.getReservaId());

            Pago pagoActualizado = pagos.findById(pago.getId())
                    .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));

            if (!pagoActualizado.isReservaConfirmada()) {
                pagoActualizado.setReservaConfirmada(true);
                pagos.saveAndFlush(pagoActualizado);
            }

        } catch (RestClientException ex) {
            log.error("No se pudo confirmar la reserva {} del pago {}",
                    pago.getReservaId(), pago.getId(), ex);
        }
    }

    // Libera una reserva cuyo pago fue rechazado o anulado.
    private void sincronizarReservaNoConcretada(Pago pago) {
        if (pago.getEstado() != EstadoPago.ANULADO && pago.getEstado() != EstadoPago.RECHAZADO) {
            return;
        }

        if (pago.isReservaNoConcretada()) {
            return;
        }

        try {
            reservaClient.noConcretarPago(pago.getReservaId());

            Pago pagoActualizado = pagos.findById(pago.getId())
                    .orElseThrow(() -> new NoSuchElementException("Pago no encontrado"));

            if (!pagoActualizado.isReservaNoConcretada()) {
                pagoActualizado.setReservaNoConcretada(true);
                pagos.saveAndFlush(pagoActualizado);
            }

        } catch (RestClientException ex) {
            log.error("No se pudo liberar la reserva {} del pago {}",
                    pago.getReservaId(), pago.getId(), ex);
        }
    }

    // Crea una transacción Webpay para una reserva validada por reserva-service.
    public InicioResponse crear(CrearPago request) throws Exception {
        if (pagos.existsByReservaIdAndEstado(request.reservaId(), EstadoPago.APROBADO)) {
            throw new IllegalStateException("La reserva ya tiene un pago aprobado");
        }

        boolean tienePagoPendiente = pagos.findByReservaIdOrderByIdDesc(request.reservaId())
                .stream()
                .anyMatch(pago -> pago.getEstado() == EstadoPago.PENDIENTE);

        if (tienePagoPendiente) {
            throw new IllegalStateException("La reserva ya tiene una transacción pendiente");
        }

        Pago pago = new Pago();
        pago.setReservaId(request.reservaId());
        pago.setUsuarioId(request.usuarioId());
        pago.setMonto(MONTO_SERVICIO);
        pago.setOrdenCompra("SE" + UUID.randomUUID().toString().replace("-", "").substring(0, 20));

        pagos.saveAndFlush(pago);

        try {
            var resultado = transaccion().create(
                    pago.getOrdenCompra(), "reserva-" + pago.getReservaId(), pago.getMonto(), returnUrl);

            pago.setToken(resultado.getToken());
            pagos.save(pago);

            return new InicioResponse(dto(pago), resultado.getUrl(), resultado.getToken());

        } catch (Exception ex) {
            pago.setEstado(EstadoPago.REVISION_MANUAL);
            pagos.save(pago);
            throw ex;
        }
    }

    // Confirma el resultado de Webpay y sincroniza el estado de la reserva.
    public PagoResponse confirmar(String token) throws Exception {
        Pago pago = pagos.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Token de pago desconocido"));

        if (pago.getEstado() == EstadoPago.APROBADO) {
            sincronizarReserva(pago);
            return dto(pago);
        }

        if (pago.getEstado() == EstadoPago.RECHAZADO) {
            sincronizarReservaNoConcretada(pago);
            return dto(pago);
        }

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new IllegalStateException("El pago ya fue procesado");
        }

        var resultado = transaccion().commit(token);

        if (!pago.getOrdenCompra().equals(resultado.getBuyOrder())
                || Double.compare(resultado.getAmount(), pago.getMonto().doubleValue()) != 0) {
            pago.setEstado(EstadoPago.REVISION_MANUAL);
            pagos.saveAndFlush(pago);
            throw new IllegalStateException("Datos de transacción inconsistentes; requiere revisión");
        }

        boolean aprobado = "AUTHORIZED".equals(resultado.getStatus())
                && resultado.getResponseCode() == 0;

        pago.setEstado(aprobado ? EstadoPago.APROBADO : EstadoPago.RECHAZADO);
        pago.setCodigoAutorizacion(resultado.getAuthorizationCode());
        pagos.saveAndFlush(pago);

        if (aprobado) {
            sincronizarReserva(pago);
        } else {
            sincronizarReservaNoConcretada(pago);
        }

        return dto(pago);
    }

    // Procesa la anulación de un pago y solicita liberar su reserva.
    public PagoResponse anularPago(String token) throws Exception {
        Pago pago = pagos.findByToken(token)
                .orElseThrow(() -> new NoSuchElementException("Token de pago desconocido"));

        if (pago.getEstado() == EstadoPago.APROBADO) {
            sincronizarReserva(pago);
            return dto(pago);
        }

        if (pago.getEstado() == EstadoPago.ANULADO) {
            sincronizarReservaNoConcretada(pago);
            return dto(pago);
        }

        if (pago.getEstado() != EstadoPago.PENDIENTE) {
            throw new IllegalStateException("El pago ya fue procesado");
        }

        String estadoWebpay = consultarEstadoWebpay(token);

        if ("AUTHORIZED".equals(estadoWebpay)) {
            throw new IllegalStateException("La transacción figura como autorizada en Transbank");
        }

        if (!"FAILED".equals(estadoWebpay)) {
            throw new IllegalStateException(
                    "La transacción todavía no tiene un resultado definitivo de fallo");
        }

        pago.setEstado(EstadoPago.ANULADO);
        pagos.saveAndFlush(pago);

        sincronizarReservaNoConcretada(pago);

        return dto(pago);
    }

    // Solicita una devolución a Transbank y registra su estado.
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

    // Lista las solicitudes de devolución asociadas a un pago.
    public List<DevolucionResponse> devoluciones(Long pagoId) {
        obtener(pagoId);

        return devoluciones.findByPagoId(pagoId).stream()
                .map(d -> new DevolucionResponse(d.getId(), pagoId,
                        d.getMonto(), d.getEstado(), d.getTipo()))
                .toList();
    }
}
