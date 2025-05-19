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

import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.UsuarioServicio;


/**
 * Clase controlador, contiene los endpoints por los que se acceden 
 * msm - 040224
 */


@Controller
public class PaginaPrincipal {
	
	private static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class); //Instancia de clase para generar logs
	Authentication credencialesSesion; //Variable que se usa para guardar los datos de una sesion
	String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
	@Autowired 
	private UsuarioServicio usuarioServicio;
	@Autowired
	private Environment env;
	
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
	    // 1) Perfil activo (si no hay ninguno, "default")
	    String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);

	    // 2) Usuario (cuando haya)
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	        nombreUsuarioLog = sesion.getNombre();
	    } else {
	        nombreUsuarioLog = "El usuario";
	    }

	    log.info(nombreUsuarioLog + " accedió a la página principal");
	    return "bienvenida";
	}


}
