package com.sanaesencia.usuario.mapper;

import org.springframework.stereotype.Component;

import com.sanaesencia.usuario.model.dto.UsuarioRequest;
import com.sanaesencia.usuario.model.dto.UsuarioResponse;
import com.sanaesencia.usuario.model.dto.UsuarioUpdateRequest;
import com.sanaesencia.usuario.model.entity.Usuario;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setCiudad(request.ciudad());
        usuario.setCorreo(request.correo().trim().toLowerCase());
        return usuario;
    }

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTelefono(),
                usuario.getFechaNacimiento(),
                usuario.getCiudad(),
                usuario.getCorreo(),
                usuario.getRol()
        );
    }

    public void actualizarPerfil(UsuarioUpdateRequest request, Usuario usuario) {
        usuario.setNombre(request.nombre());
        usuario.setApellido(request.apellido());
        usuario.setTelefono(request.telefono());
        usuario.setFechaNacimiento(request.fechaNacimiento());
        usuario.setCiudad(request.ciudad());
        usuario.setCorreo(request.correo().trim().toLowerCase());
    }
}
