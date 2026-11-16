package com.sanaesencia.servicio.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sanaesencia.servicio.model.entity.*;
import com.sanaesencia.servicio.repository.*;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner cargarDatos(EspecialidadRepository especialidades, ServicioRepository servicios) {
        return args -> {
            if (especialidades.count() == 0) {
                Especialidad psicologia = new Especialidad();
                psicologia.setNombre("Psicología");
                psicologia.setDescripcion("Atención y acompañamiento psicológico.");
                especialidades.save(psicologia);

                Especialidad nutricion = new Especialidad();
                nutricion.setNombre("Nutrición");
                nutricion.setDescripcion("Evaluación y orientación nutricional.");
                especialidades.save(nutricion);
            }

            if (servicios.count() == 0) {
                var lista = especialidades.findAll();
                Servicio consulta = new Servicio();
                consulta.setNombre("Consulta psicológica");
                consulta.setEspecialidadId(lista.get(0).getId());
                consulta.setDuracionMinutos(60);
                consulta.setDescripcion("Consulta individual de psicología.");
                servicios.save(consulta);

                Servicio evaluacion = new Servicio();
                evaluacion.setNombre("Evaluación nutricional");
                evaluacion.setEspecialidadId(lista.get(1).getId());
                evaluacion.setDuracionMinutos(45);
                evaluacion.setDescripcion("Evaluación nutricional inicial.");
                servicios.save(evaluacion);
            }
        };
    }
}
