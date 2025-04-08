package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Clase controlador, contiene los endpoints por los que se acceden 
 * msm - 040224
 */


@Controller
public class PaginaPrincipal {
	
	public static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class);
	 
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
		log.info("El usuario entro en la pagina principal");
		modelo.addAttribute("mensaje", "¡Bienvenido a nuestra tienda de manga!");
		return "bienvenida";

	}

}
