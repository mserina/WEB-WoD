package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para la gestión de inicio de sesión.
 * msm - 050325
 */
@Controller
public class InicioSesionControlador {

	
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
    	log.info("El usuario accedio al login");
        if (logout != null) {
        	log.info("El usuario salio de la sesion");
            model.addAttribute("logoutMessage", "Has cerrado sesión exitosamente.");
        }
        return "login";
    }
}
