
package com.sanaesencia.usuario.mapper;

import org.springframework.stereotype.Component;

import com.sanaesencia.usuario.model.dto.UsuarioRequest;
import com.sanaesencia.usuario.model.dto.UsuarioResponse;
import com.sanaesencia.usuario.model.entity.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {

        Usuario usuario = new Usuario();

        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setCorreo(request.correo().trim().toLowerCase());
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setDireccion(request.direccion());
        usuario.setTelefono(request.telefono());

        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getFechaNacimiento(),
                usuario.getDireccion(),
                usuario.getTelefono(),
                usuario.getRol().name()
        );
    }
}
