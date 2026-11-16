package com.sanaesencia.profesional.config;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.sanaesencia.profesional.model.entity.Profesional;
import com.sanaesencia.profesional.repository.ProfesionalRepository;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner cargarDatos(ProfesionalRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Profesional psicologa = new Profesional();
                psicologa.setNombre("Camila González");
                psicologa.setTituloProfesional("Psicóloga");
                psicologa.setEspecialidadesIds(List.of(1L));
                psicologa.setModalidades(List.of("Online", "Presencial"));
                psicologa.setEstadoAgenda("DISPONIBLE");
                repository.save(psicologa);

                Profesional nutricionista = new Profesional();
                nutricionista.setNombre("Valentina Muñoz");
                nutricionista.setTituloProfesional("Nutricionista");
                nutricionista.setEspecialidadesIds(List.of(2L));
                nutricionista.setModalidades(List.of("Online"));
                nutricionista.setEstadoAgenda("DISPONIBLE");
                repository.save(nutricionista);
            }
        };
    }
}
