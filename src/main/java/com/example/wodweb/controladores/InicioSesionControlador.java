package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.UsuarioServicio;

/**
 * Controlador para la gestión de inicio de sesión.
 * msm - 050325
 */
@Controller
public class InicioSesionControlador {

	private static final Logger log = LoggerFactory.getLogger("logMensajes"); //Instancia de clase para generar logs
	Authentication credencialesSesion; //Variable que se usa para guardar los datos de una sesion
	String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
	@Autowired
	UsuarioServicio usuarioServicio;
	@Autowired
	private Environment env;
	
	/* /////////////////////////////////// */
    /*             METODOS                  */
    /* //////////////////////////////////// */
	
    /**
     * Método que maneja la vista de inicio de sesión.
     * msm - 050325
     * @param model Modelo de datos para la vista.
     * @param logout Parámetro opcional para indicar si se cerró sesión.
     * @return Retorna la vista "login".
     */
	@GetMapping("/login")
	public String login(Model modelo, @RequestParam(required = false) String logout, @RequestParam(required = false) String error) {
	    
	   // Recuperamos la autenticación actual (puede ser Anonymous si nadie ha iniciado sesión)
		credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
		
		// Si viene login?error en la URL, mostramos mensaje un mensaje de error
		if (error != null) {
	        modelo.addAttribute("mensajeError", error);
	    }
		
	  // Si viene ?logout en la URL, mostramos mensaje de “Has cerrado sesión”
	    if (logout != null) {
	    	log.info("Se cerro la sesion");
	        modelo.addAttribute("logoutMessage", "Has cerrado sesión exitosamente.");
	    
	    } else {
	      // Si no es un logout, intentamos averiguar quién es el usuario actual
	    	if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
	    		// 1) Perfil activo (si no hay ninguno, "default")
	    	    String perfil = env.getActiveProfiles().length > 0
	    	                    ? env.getActiveProfiles()[0]
	    	                    : "default";
	    	    modelo.addAttribute("perfilActivo", perfil);
	    		
	    		// Usuario autenticado correctamente
	    		SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
	    		String correo = sesion.getUsername();
	            UsuarioDto u = usuarioServicio.buscarUsuario(correo);
	            modelo.addAttribute("usuario", u);
	            nombreUsuarioLog = sesion.getNombre();
	        }
	    	else {
	    	   // No hay usuario autenticado o es anónimo
	    		nombreUsuarioLog = "El usuario";
	    	}
	    	log.info(nombreUsuarioLog + " accedio a login");
	    }
	    
	    return "login";
	}

}



