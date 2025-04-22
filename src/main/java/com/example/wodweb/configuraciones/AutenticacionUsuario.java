package com.example.wodweb.configuraciones;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.wodweb.dtos.InicioSesionDto;
import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.InicioSesionServicio;

/**
 * Clase que implementa la autenticación personalizada en Spring Security.
 * Se encarga de validar las credenciales del usuario a través del servicio de autenticación.
 * 
 * msm - 060325
 */
@Component
public class AutenticacionUsuario implements AuthenticationProvider {

    /** Servicio encargado de validar las credenciales del usuario */
    @Autowired
    private InicioSesionServicio inicioSesionServicio;

    
	private static final Logger log = LoggerFactory.getLogger(AutenticacionUsuario.class);

    
    /* /////////////////////////////////// */
    /*               METODOS                */
    /* //////////////////////////////////// */
    
    /**
     * Método encargado de autenticar a un usuario en la aplicación.
     *
     * @param autenticacion Objeto que contiene las credenciales ingresadas por el usuario.
     * @return Un objeto de tipo Authentication si las credenciales son correctas.
     * @throws AuthenticationException Si las credenciales son incorrectas.
     */
    @Override
    public Authentication authenticate(Authentication autenticacion) throws AuthenticationException {
        
       // Extraer correo electrónico (username) y contraseña de la solicitud de autenticación
        String correo = autenticacion.getName();
        String contrasena = autenticacion.getCredentials().toString();

        
       // Crear un DTO con las credenciales del usuario
        InicioSesionDto credencialesUsuario = new InicioSesionDto();
        credencialesUsuario.setCorreoElectronico(correo);
        credencialesUsuario.setContrasena(contrasena);

        
       // Llamar al servicio para validar la autenticación (puede lanzar BadCredentialsException o AuthenticationServiceException)
        UsuarioDto usuarioRecogido = inicioSesionServicio.autenticarUsuario(credencialesUsuario);

        
        // Comprobar que el usuario esta verificado
        if (usuarioRecogido != null && Boolean.FALSE.equals(usuarioRecogido.getVerificado())) {
        	throw new DisabledException("Debes verificar tu cuenta antes de ingresar");
        }
        
        
       // Si el usuario es válido, asignar sus roles (permisos)
    	List<SimpleGrantedAuthority> rol = new ArrayList<>();
        
    	
    	// Convertir el tipo de usuario en un rol válido para Spring Security
        rol.add(new SimpleGrantedAuthority("ROLE_" + usuarioRecogido.getTipoUsuario().toUpperCase()));

        
        // Crear un objeto con los detalles del usuario autenticado
        SesionDto detalleUsuario = new SesionDto(
                usuarioRecogido.getCorreoElectronico(),
                usuarioRecogido.getContrasena(),
                usuarioRecogido.getNombreCompleto(),  // Se almacena el nombre del usuario
                rol
        );

        log.info(detalleUsuario.getNombre() + " ha iniciado sesion");
        
        // Retornar un token de autenticación exitoso con el usuario autenticado y sus roles
        return new UsernamePasswordAuthenticationToken(detalleUsuario, contrasena, rol);
    }

    
    
    /**
     * Método que indica si este proveedor de autenticación soporta un tipo de autenticación específica.
     * En este caso, solo soporta UsernamePasswordAuthenticationToken.
     *
     * @param autenticacion Clase de autenticación.
     * @return true si soporta la autenticación indicada, false en caso contrario.
     */
    @Override
    public boolean supports(Class<?> autenticacion) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(autenticacion);
    }
}
