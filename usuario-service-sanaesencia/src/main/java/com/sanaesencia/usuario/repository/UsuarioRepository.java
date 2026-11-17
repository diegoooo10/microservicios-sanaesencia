package com.sanaesencia.usuario.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sanaesencia.usuario.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByCorreoAndIdNot(String correo, Long id);
}
