package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.wodweb.dtos.SesionDto;


/**
 * Clase controlador, contiene los endpoints por los que se acceden 
 * msm - 040224
 */


@Controller
public class PaginaPrincipal {
	
	public static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class);
	Authentication credencialesSesion;
	String nombreUsuarioLog = "El usuario";
	 
	/* /////////////////////////////////// */
    /*             METODOS                  */
    /* //////////////////////////////////// */
	
	
	/**
	 * Maneja la página principal de bienvenida URL: http://localhost:8080/ 
	 * @param model Modelo para pasar datos a la vista
	 * @return Nombre del html de bienvenida
	 */
	@GetMapping("/")
	public String bienvenida(Model modelo) {
		
		credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
		if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            // Usamos el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
        }
		else {
    		nombreUsuarioLog = "El usuario";
    	}
        log.info(nombreUsuarioLog + " accedio a la pagina principal");
		return "bienvenida";

	}

}
