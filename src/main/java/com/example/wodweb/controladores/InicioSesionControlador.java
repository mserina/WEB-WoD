package com.example.wodweb.controladores;

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
        if (logout != null) {
            model.addAttribute("logoutMessage", "Has cerrado sesión exitosamente.");
        }
        return "login";
    }
}
