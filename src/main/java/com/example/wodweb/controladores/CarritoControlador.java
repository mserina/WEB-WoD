package com.example.wodweb.controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wodweb.dtos.ArticuloDto;
import com.example.wodweb.dtos.CarritoDto;
import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.ArticuloServicio;
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
	  Authentication autenticacion;
	  private static final Logger log = LoggerFactory.getLogger("logMensajes"); //Instancia de clase para generar logs
	  @Autowired
	  private Environment env;
	  String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
	  @Autowired
	  private ArticuloServicio articuloServicio;

	 
	 /**
	  * Añade articulos al carrito
	  * msm - 250525
	  * @param articuloId id del articulo que se vaya a agregar
	  * @param cantidad cantidad del producto
	  * @return la pagina del catalogo con la respuesta
	  */
	 @PostMapping("/anadirCarrito")
	    public String anadirAlCarrito(@RequestParam String catalogo, @RequestParam Long articuloId, @RequestParam(defaultValue = "1") Integer cantidad, RedirectAttributes mensajesRedireccion, Model modelo) {
		
		 // Usuario (cuando haya)
		    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
		        SesionDto sesion = (SesionDto) auth.getPrincipal();
		        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
		        nombreUsuarioLog = sesion.getNombre();
		    } else {
		        nombreUsuarioLog = "El usuario";
		    }

		    
		 Long usuarioSesionId = null;
		 autenticacion = SecurityContextHolder.getContext().getAuthentication();
		 
		 
		 // Verificar si el usuario está autenticado
		    if (autenticacion == null || !autenticacion.isAuthenticated() || autenticacion.getPrincipal().equals("anonymousUser")) {
		        log.warn("Intento de agregar al carrito sin iniciar sesión");
		        mensajesRedireccion.addFlashAttribute("mensajeError", "Debes iniciar sesión para agregar artículos al carrito.");
		        return "redirect:/login"; // o redirige a donde prefieras
		    }
            
		  // Una vez comprobado se recopila el id del usuario logueado
		    try {
		        SesionDto sesion = (SesionDto) autenticacion.getPrincipal();
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
            log.info(nombreUsuarioLog + " agrego el articulo " + articuloId + " en el carrito");
            return "redirect:/catalogo/" + catalogo; 
	        
            
	       // Error al convertir las credenciales de sesión (sesión inválida o no autenticada)
		    } catch (ClassCastException e) {
		        log.error("Error al obtener sesión del usuario: " + e.getMessage());
		        mensajesRedireccion.addFlashAttribute("errorSesion", "Error de sesión. Intenta iniciar sesión nuevamente.");
		        return "redirect:/login";

		        
		   // El artículo ya está en el carrito (estado no permitido)
		    } catch (IllegalStateException e) {
		        log.error("Error: El articulo " + articuloId + " ya se agrego al carrito: " + e.getMessage());
		        mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
		        return "redirect:/catalogo/" + catalogo;

		        
		   // Usuario o artículo no encontrado en la base de datos
		    } catch (NoSuchElementException e) {
		        log.error("Error: No se encontro el usuario o el articulo: " + e.getMessage());
		        mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
		        return "redirect:/login";

		        
		   // Datos inválidos (por ejemplo, cantidad negativa)
		    } catch (IllegalArgumentException e) {
		        log.error("Error: la cantidad es negativa: " + e.getMessage());
		        mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
		        return "redirect:/login";

		        
		    // Cualquier otro error inesperado al conectar con la API
		    } catch (Exception e) {
		        log.error("Error en la API: " + e.getMessage());
		        mensajesRedireccion.addFlashAttribute("mensajeError", "Error al conectar con la API.");
		        return "redirect:/login";
		    }
		    
	 	}
	 
	 
	 /**
	  * 
	  * Ver el carrito del usuario logueado
	  * msm - 270525 
	  * @param modelo 
	  * @param mensajesRedireccion Para imprimir mensajes por pantalla
	  * @return devuelve la direccion para ver la pagina de carrito
	  */
	 @GetMapping("/verCarrito")
	    public String verCarrito(Model modelo, RedirectAttributes mensajesRedireccion) {
	        //  Comprobar sesión
	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
	            log.warn("Usuario anónimo intentó ver el carrito");
	            mensajesRedireccion.addFlashAttribute("mensajeError", "Debes iniciar sesión para ver tu carrito.");
	            return "redirect:/login";
	        }

	     // Perfil activo (si no hay ninguno, "default")
		    String perfil = env.getActiveProfiles().length > 0
		                    ? env.getActiveProfiles()[0]
		                    : "default";
		    modelo.addAttribute("perfilActivo", perfil);
		    
	        try {
	            // Obtener usuario y su ID
	            SesionDto sesion = (SesionDto) auth.getPrincipal();
	            UsuarioDto usuario = usuarioServicio.buscarUsuario(sesion.getUsername());
	            Long usuarioId = usuario.getId();
	            modelo.addAttribute("usuario", usuario);

	            // Llamar al servicio para traer los artículos del carrito
	            List<Map<String, ?>> carritoArticulos = carritoServicio.obtenerCarritoDeUsuario(usuarioId);

	            // Calcular total
	            double total = carritoArticulos.stream()
	                    .mapToDouble(m -> ((Number) m.get("subtotal")).doubleValue())
	                    .sum();

	            //  Añadir datos al modelo
	            modelo.addAttribute("elementosCarrito", carritoArticulos);
	            modelo.addAttribute("totalCarrito", total);

	            // Muestra el html del carrito
	            return "carrito";

	        } catch (NoSuchElementException e) {
	            // Carrito o artículo no encontrado
	            log.error("Error al cargar carrito: {}", e.getMessage());
	            mensajesRedireccion.addFlashAttribute("mensajeError", e.getMessage());
	            return "redirect:/catalogo/manga";

	        } catch (Exception e) {
	            // Cualquier otro fallo
	            log.error("Error inesperado al obtener carrito", e);
	            mensajesRedireccion.addFlashAttribute("mensajeError", "No se pudo cargar el carrito. Intenta de nuevo.");
	            return "redirect:/catalogo/manga";
	        }
	    }
	 
	 /**
	  * Actualiza la cantidad 
	  * msm - 270525
	  * @param elementoCarritoId 
	  * @param articuloId 
	  * @param cantidad  
	  * @param mensajeRedireccion
	  * @return Devuelve la vista de carrito
	  */
	 @PostMapping("/carrito/actualizar")
	    public String actualizarCantidad(@RequestParam Long elementoCarritoId, @RequestParam Long articuloId, @RequestParam Integer cantidad, RedirectAttributes mensajeRedireccion) {

	        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	        // Controlamos que el usuario esté autenticado
	        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
	            log.warn("Intento de actualizar carrito sin sesión válida");
	            mensajeRedireccion.addFlashAttribute("mensajeError", "Debes iniciar sesión para modificar el carrito.");
	            return "redirect:/login";
	        }

	        try {
	            // Llamada al servicio frontend que reenvía a la API
	            carritoServicio.actualizarCantidad(elementoCarritoId, articuloId, cantidad);
	            mensajeRedireccion.addFlashAttribute("mensajeExito", "Cantidad actualizada correctamente.");

	        } catch (IllegalArgumentException e) {
	            log.warn("Cantidad inválida al actualizar carrito: {}", e.getMessage());
	            mensajeRedireccion.addFlashAttribute("mensajeError", e.getMessage());

	        } catch (NoSuchElementException e) {
	            log.error("Elemento de carrito no encontrado: {}", e.getMessage());
	            mensajeRedireccion.addFlashAttribute("mensajeError", e.getMessage());

	        } catch (Exception e) {
	            log.error("Error inesperado al actualizar carrito", e);
	            mensajeRedireccion.addFlashAttribute("mensajeError", "No se pudo actualizar el carrito. Intenta de nuevo.");
	        }

	        // Siempre redirigimos a verCarrito para recargar la vista con los nuevos datos
	        return "redirect:/verCarrito";
	    }
	}   
