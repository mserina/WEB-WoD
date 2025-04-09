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
	public String login(Model model, @RequestParam(required = false) String logout) {
		
		credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
		
	    if (logout != null) {
	    	SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            nombreUsuarioLog = sesion.getNombre();
            
	        log.info(nombreUsuarioLog +" salio de la sesion");
	        model.addAttribute("logoutMessage", "Has cerrado sesión exitosamente.");
	    } else {
	    	if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
	            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
	            nombreUsuarioLog = sesion.getNombre();
	        }
	        log.info(nombreUsuarioLog + " accedio a login");
	    }
	    return "login";
	}
/**
 * if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            // Usamos el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
        }
        log.info(nombreUsuarioLog + " accedio a registro");
 */
}



