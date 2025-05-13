package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.ArticuloServicio;
import com.example.wodweb.servicios.UsuarioServicio;

/**
 * Controlador para la gestión de articulo.
 * msm - 130525
 */
@Controller
public class ArticuloControlador {
	
	@Autowired
	private ArticuloServicio articuloServicio;
	@Autowired
	private UsuarioServicio usuarioServicio; //Inyeccion del servicio para usuarios
	private static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class); //Instancia de clase para generar logs
	Authentication credencialesSesion; //Variable que se usa para guardar los datos de una sesion
	String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
    
	
	
	/**
     * Método para obtener la lista de articulos y enviarla a la vista.
     * msm - 130525
     * @param model Modelo para pasar los datos a la vista.
     * @return Devuelve la vista "articulos".
     */
    @GetMapping("/admin/obtenerArticulos")
    public String obtenerArticulos(Model modelo) {
    	//Se guarda los datos de la sesion 
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
    	modelo.addAttribute("articulos", articuloServicio.obtenerArticulos());
        
    	//Se comprueba que la sesion no sea null
        if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            String correo = sesion.getUsername();
            UsuarioDto u = usuarioServicio.buscarUsuario(correo);
            modelo.addAttribute("usuario", u);
            // En caso de que se cumpla las condiciones, se usa el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
            modelo.addAttribute("auth", sesion); // Enviar usuario autenticado a la vista
        }
        else {
    		nombreUsuarioLog = "El usuario";
    	}
        log.info(nombreUsuarioLog + " accedio a la lista de articulos");
        return "articulos";
    }
}
