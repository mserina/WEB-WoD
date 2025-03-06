package com.example.wodweb.controladores;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


/**
 * Clase controlador, contiene los endpoints por los que se acceden 
 * msm - 040224
 */
@Controller
public class PaginaPrincipal {

	
	/**
	 * Maneja la página principal de bienvenida URL: http://localhost:8080/ 
	 * @param model Modelo para pasar datos a la vista
	 * @return Nombre del html de bienvenida
	 */
	@GetMapping("/")
	public String bienvenida(Model modelo) {
		modelo.addAttribute("mensaje", "¡Bienvenido a nuestra tienda de manga!");
		return "bienvenida";

	}

}
