package com.example.wodweb.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.CarritoServicio;
import com.example.wodweb.servicios.UsuarioServicio;

/**
 * Controlador para la gestión del carrito.
 * msm - 250525
 */
public class CarritoControlador {

	  @Autowired
	  private CarritoServicio carritoServicio;
	  @Autowired
	  private UsuarioServicio usuarioServicio;
	  Authentication credencialesSesion;

	 
	 /**
	  * Añade articulos al carrito
	  * msm - 250525
	  * @param articuloId id del articulo que se vaya a agregar
	  * @param cantidad cantidad del producto
	  * @param modelo mensaje que se imprira por pantalla
	  * @return la pagina del catalogo con la respuesta
	  */
	 @PostMapping("/anadirCarrito")
	    public String anadirAlCarrito(@RequestParam Long articuloId, @RequestParam(defaultValue = "1") Integer cantidad, Model modelo, RedirectAttributes mensajesRedireccion) {
		 Long usuarioSesionId = null;
		 credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
		 
	        // Obtener usuario de la sesión 
	    	SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            
	    	List<UsuarioDto> usuarios = usuarioServicio.obtenerUsuarios();
            for (UsuarioDto usuario : usuarios) {
            	if (usuario.getCorreoElectronico().equals(sesion.getUsername())) {
            		
            		usuarioSesionId = usuario.getId();
                    break;
                }
            }
                
            carritoServicio.agregarAlCarrito(usuarioSesionId, articuloId, cantidad);           
            mensajesRedireccion.addFlashAttribute("mensajeArticulo", "Articulo agregado al carrito.");
	        return "redirect:/catalogo/"; 
	    }
}
