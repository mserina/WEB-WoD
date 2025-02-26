package com.example.wodweb.configuraciones;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.wodweb.dtos.InicioSesionDto;
import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.InicioSesionServicio;

@Component
public class AutenticacionUsuario implements AuthenticationProvider {

    // Inyectamos el servicio que se encarga de autenticar (por ejemplo, llamando a una API o base de datos)
    @Autowired
    private InicioSesionServicio inicioSesionServicio;

    @Override
    public Authentication authenticate(Authentication autenticacion) throws AuthenticationException {
        // Extraemos el email (username) y la contraseña de la petición de autenticación
        String correo = autenticacion.getName();
        String contrasena = autenticacion.getCredentials().toString();

        
        // Creamos un DTO con los datos del usuario para pasarlo al servicio de autenticación
        InicioSesionDto credencialesUsuario = new InicioSesionDto();
        credencialesUsuario.setCorreoElectronico(correo);
        credencialesUsuario.setContrasena(contrasena);

        
        // Llamamos al servicio para autenticar al usuario
        UsuarioDto usuarioRecogido = inicioSesionServicio.autenticarUsuario(credencialesUsuario);
        
        
        if (usuarioRecogido != null) {
            // Si el usuario se autentica correctamente, creamos una lista de autoridades (roles)
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            // Convertimos el rol del usuario a un formato que Spring Security entiende, 
            // normalmente anteponiendo "ROLE_" al rol (por ejemplo, "ADMIN" se convierte en "ROLE_ADMIN")
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuarioRecogido.getTipoUsuario().toUpperCase()));

            
            // Creamos un CustomUserDetails con la información adicional (nombre)
            SesionDto detalleUsuario = new SesionDto(
                    usuarioRecogido.getCorreoElectronico(),
                    usuarioRecogido.getContrasena(),
                    usuarioRecogido.getNombreCompleto(),  // Aquí se guarda el nombre
                    authorities
            );
            
            // Retornamos un token de autenticación exitoso con el email, la contraseña y las autoridades
            return new UsernamePasswordAuthenticationToken(detalleUsuario, contrasena, authorities);
        
        } else {
            // Si la autenticación falla, se lanza una excepción que Spring Security maneja internamente
            throw new BadCredentialsException("Credenciales incorrectas");
        }
    }

    @Override
    public boolean supports(Class<?> autenticacion) {
        // Indica que este proveedor soporta autenticaciones de tipo UsernamePasswordAuthenticationToken.
        // Es obligatorio implementarlo para que Spring sepa qué tipo de autenticación puede manejar.
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(autenticacion);
    }
}
