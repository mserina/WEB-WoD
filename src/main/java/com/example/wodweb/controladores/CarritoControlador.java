package com.example.wodweb.controladores;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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

@Controller
public class CarritoControlador {

	  @Autowired
	  private CarritoServicio carritoServicio;
	  @Autowired
	  private UsuarioServicio usuarioServicio;
	  Authentication credencialesSesion;
	  private static final Logger log = LoggerFactory.getLogger("logMensajes"); //Instancia de clase para generar logs


	 
	 /**
	  * Añade articulos al carrito
	  * msm - 250525
	  * @param articuloId id del articulo que se vaya a agregar
	  * @param cantidad cantidad del producto
	  * @return la pagina del catalogo con la respuesta
	  */
	 @PostMapping("/anadirCarrito")
	    public String anadirAlCarrito(@RequestParam String catalogo, @RequestParam Long articuloId, @RequestParam(defaultValue = "1") Integer cantidad, RedirectAttributes mensajesRedireccion) {
		 log.info(catalogo);
		 Long usuarioSesionId = null;
		 credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
		 
		 // Verificar si el usuario está autenticado
		    if (credencialesSesion == null || !credencialesSesion.isAuthenticated() || credencialesSesion.getPrincipal().equals("anonymousUser")) {
		        log.warn("Intento de agregar al carrito sin iniciar sesión");
		        mensajesRedireccion.addFlashAttribute("mensajeError", "Debes iniciar sesión para agregar artículos al carrito.");
		        return "redirect:/login"; // o redirige a donde prefieras
		    }
            
		  // Una vez comprobado se recopila el id del usuario logueado
		    try {
		        SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
		        List<UsuarioDto> usuarios = usuarioServicio.obtenerUsuarios();
		        usuarioSesionId = null;
		        
		        for (UsuarioDto usuario : usuarios) {
		            if (usuario.getCorreoElectronico().equals(sesion.getUsername())) {
		                usuarioSesionId = usuario.getId();
		                break;
		            }
		        }
                
		    // Una vez se tenga los campos necesarios, se llama al metodo para añadir articulos, pasandole los atributos corresponidnetes    
            carritoServicio.agregarAlCarrito(usuarioSesionId, articuloId, cantidad);           
            mensajesRedireccion.addFlashAttribute("mensajeArticulo", "Articulo agregado al carrito.");
	        return "redirect:/catalogo/" + catalogo; 
	        
	       // Error al convertir las credenciales de sesión (sesión inválida o no autenticada)
		    } catch (ClassCastException e) {
		        log.error("Error al obtener sesión del usuario: " + e.getMessage());
		        mensajesRedireccion.addFlashAttribute("errorSesion", "Error de sesión. Intenta iniciar sesión nuevamente.");
		        return "redirect:/login";

		   // El artículo ya está en el carrito (estado no permitido)
		    } catch (IllegalStateException e) {
		        mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
		        return "redirect:/catalogo/" + catalogo;

		   // Usuario o artículo no encontrado en la base de datos
		    } catch (NoSuchElementException e) {
		        mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
		        return "redirect:/login";

		   // Datos inválidos (por ejemplo, cantidad negativa)
		    } catch (IllegalArgumentException e) {
		        mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
		        return "redirect:/login";

		    // Cualquier otro error inesperado al conectar con la API
		    } catch (Exception e) {
		        mensajesRedireccion.addFlashAttribute("mensajeError", "Error al conectar con la API.");
		        return "redirect:/login";
		    }
		    
	 	}
	}   
