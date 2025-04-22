package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.wodweb.dtos.SesionDto;

/**
 * Controlador para la gestión de inicio de sesión.
 * msm - 050325
 */
@Controller
public class InicioSesionControlador {

	public Authentication credencialesSesion;
	String nombreUsuarioLog = "El usuario";
	public static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class);
	
	
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
	public String login(Model model, @RequestParam(required = false) String logout, @RequestParam(required = false) String error) {
	    
	   // Recuperamos la autenticación actual (puede ser Anonymous si nadie ha iniciado sesión)
		credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
		
		// Si viene login?error en la URL, mostramos mensaje un mensaje de error
		if (error != null) {
	        model.addAttribute("mensajeError", error);
	    }
		
	  // Si viene ?logout en la URL, mostramos mensaje de “Has cerrado sesión”
	    if (logout != null) {
	        model.addAttribute("logoutMessage", "Has cerrado sesión exitosamente.");
	    
	    } else {
	      // Si no es un logout, intentamos averiguar quién es el usuario actual
	    	if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
	    	  // Usuario autenticado correctamente
	    		SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
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



