
package com.sanaesencia.usuario.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String remitente;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String remitente
    ) {
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    public void enviarRecuperacion(
            String destinatario,
            String enlace
    ) {
        SimpleMailMessage mensaje = new SimpleMailMessage();

        mensaje.setFrom(remitente);
        mensaje.setTo(destinatario);
        mensaje.setSubject("SanaEsencia - Recuperación de contraseña");

        mensaje.setText(
                "Hola,\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña.\n\n"
                + "Utiliza el siguiente enlace:\n"
                + enlace + "\n\n"
                + "Este enlace tiene una duración de 30 minutos.\n\n"
                + "Si no solicitaste este cambio, puedes ignorar este correo.\n\n"
                + "Equipo SanaEsencia"
        );

        mailSender.send(mensaje);
    }
}
