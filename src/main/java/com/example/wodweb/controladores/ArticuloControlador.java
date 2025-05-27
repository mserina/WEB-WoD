package com.example.wodweb.controladores;

import java.io.IOException;
import java.util.List;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wodweb.dtos.ArticuloDto;
import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.excepciones.CorreoExistenteExcepcion;
import com.example.wodweb.servicios.ArticuloServicio;
import com.example.wodweb.servicios.UsuarioServicio;

/**
 * Controlador para la gestión de articulo.
 * msm - 130525
 */
@Controller
public class ArticuloControlador {
	
	@Autowired
	private ArticuloServicio articuloServicio; // Inyeccion del servicio de articulos
	@Autowired
	private UsuarioServicio usuarioServicio; //Inyeccion del servicio para usuarios
	@Autowired
	private Environment env; 
	private static final Logger log = LoggerFactory.getLogger("logMensajes"); //Instancia de clase para generar logs
	Authentication credencialesSesion; //Variable que se usa para guardar los datos de una sesion
	String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
	
	
	
	/**
	 * Muestra el catalogo de mangas
	 * msm - 260525
	 * @param modelo Modelo para pasar datos a la vista.
	 * @return Devuelve la vista del catalogo
	 */
	@GetMapping("/catalogo/manga")
	public String catalogoManga(Model modelo) {
		
    // Determina el perfil activo (dev, prod, etc.) y lo añade al modelo
		String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);

	  // Verifica si hay un usuario autenticado y añade su info al modelo
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	    }
	    
	 // Obtiene todos los artículos de tipo "manga" y los pasa a la vista
	    List<ArticuloDto> articulos = articuloServicio.obtenerPorTipo("manga");
	    modelo.addAttribute("articulosLista", articulos);
	    modelo.addAttribute("titulo", "Manga");

	    log.info(nombreUsuarioLog + " accedió al catalogo de manga");
	    return "catalogo";
	}
	

	/**
	 * Muestra el catalogo de figuras
	 * msm - 270525
	 * @param modelo Modelo para pasar los datos a la vista.
	 * @return Devuelve la vista del catalogo
	 */
	@GetMapping("/catalogo/figura")
	public String catalogoFigura(Model modelo) {
	    
	    // Determina el perfil activo (dev, prod, etc.) y lo añade al modelo
		String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);
	    
		// Verifica si hay un usuario autenticado y añade su info al modelo
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	    }
	    
	    // Obtiene todos los artículos de tipo "manga" y los pasa a la vista
		List<ArticuloDto> articulos = articuloServicio.obtenerPorTipo("figura");
	    modelo.addAttribute("titulo", "Figura");
        modelo.addAttribute("articulosLista", articulos); // ← Aquí se inyecta al modelo
        
        log.info(nombreUsuarioLog + " accedió al catalogo de figura");
	    return "catalogo";
	}
	
	/**
	 * Muestra el catalogo de posters
	 * msm - 270525
	 * @param modelo Modelo para pasar los datos a la vista.
	 * @return Devuelve la vista del catalogo
	 */
	@GetMapping("/catalogo/poster")
	public String catalogoPoster(Model modelo) {
	    
	    // Determina el perfil activo (dev, prod, etc.) y lo añade al modelo
		String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);
	    
		// Verifica si hay un usuario autenticado y añade su info al modelo
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	    }
	    
	    // Obtiene todos los artículos de tipo "manga" y los pasa a la vista
		List<ArticuloDto> articulos = articuloServicio.obtenerPorTipo("poster");
	    modelo.addAttribute("titulo", "Poster");
	    modelo.addAttribute("articulosLista", articulos); // ← Aquí se inyecta al modelo
	    
	    log.info(nombreUsuarioLog + " accedió al catalogo de poster");
	    return "catalogo";
	}
	
	
	/**
     * Método para obtener la lista de articulos y enviarla a la vista.
     * msm - 130525
     * @param model Modelo para pasar los datos a la vista.
     * @return Devuelve la vista "articulos".
     */
    @GetMapping("/admin/obtenerArticulos")
    public String obtenerArticulos(Model modelo) {
        
    	// Determina el perfil activo (dev, prod, etc.) y lo añade al modelo
	    String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);
	  
	    
	    //Se guarda los datos de la sesion 
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
    	
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
        
        //Se guarda y muestra la lista de articulos en pantalla
    	modelo.addAttribute("articulos", articuloServicio.obtenerArticulos());
        log.info(nombreUsuarioLog + " accedio a la lista de articulos");
        return "articulos";
    }
    
    
    /**
     * Método para editar un articulo.
     * msm - 050325
     * @param nombre nombre del usuario a modificar.
     * @param campo Campo a actualizar.
     * @param nuevoValor Nuevo valor a asignar.
     * @param mensajesRedireccion Para mandar mensajes en pantalla tras una redireccion
     * @return Redirige a la vista con la lista de articulos.
     */
    @PostMapping("/admin/editarArticulo")
    public String editarArticulo(@RequestParam String nombre, @RequestParam String campo, @RequestParam(required = false) String nuevoValor,@RequestParam(name = "foto", required = false) MultipartFile foto,RedirectAttributes mensajesRedireccion) {
        
    	boolean actualizado;
        if ("foto".equals(campo) && foto != null && !foto.isEmpty()) {
            // Llamamos a la versión del servicio que recibe el archivo
            actualizado = articuloServicio.editarArticulo(nombre, campo, null, foto);
        } else {
            // Llamamos a la versión que sólo recibe texto
            actualizado = articuloServicio.editarArticulo(nombre, campo, nuevoValor, null);
        }

        if (actualizado) {
        	mensajesRedireccion.addFlashAttribute("mensaje", "Artículo actualizado con éxito.");
        	mensajesRedireccion.addFlashAttribute("tipoMensaje", "success");
        } else {
        	mensajesRedireccion.addFlashAttribute("mensaje", "Error al actualizar artículo.");
        	mensajesRedireccion.addFlashAttribute("tipoMensaje", "danger");
        }
        log.info("Se modificó el campo " + campo + " del artículo " + nombre);
        return "redirect:/admin/obtenerArticulos";
    }
    
    
    /**
     * Borra un articulo
     * msm - 270525
     * @param id El id del articulo a borrar
     * @param mensajesRedireccion Para mandar mensajes en pantalla tras una redireccion
     * @return Devlve la vista de la lista de articulo (se visualizar que el articulo fue borrado)
     */
    @PostMapping("/admin/borrarArticulo")
    public String borrarArticulo(@RequestParam Long id, RedirectAttributes mensajesRedireccion) {
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
    	String nombreArticuloBorrado = "";
    	
    	boolean borrado = articuloServicio.borrarArticulo(id); // Llamar al servicio para eliminar el articulo
        log.info(nombreUsuarioLog + " elimino el articulo " + nombreArticuloBorrado);
        
        	if (borrado) {
        		mensajesRedireccion.addFlashAttribute("mensaje", "Articulo borrado correctamente.");
            } else {
            	mensajesRedireccion.addFlashAttribute("mensaje", "Error al borrar el articulo.");
            }
        
        return "redirect:/admin/obtenerArticulos";
    }
    
    
    /**
     * Para crear un nuevo articulo
     * msm - 270525
     * @param articuloNuevo El articulo que se quiere agregar
     * @param modelo Modelo para pasar los datos a la vista.
     * @param mensajesRedireccion Para mandar mensajes en pantalla tras una redireccion
     * @param foto La foto de articulo
     * @return Devuelve la vista de la lista de articulos
     */
    @PostMapping("/admin/crearArticulo")
    public String registrarArticulo(ArticuloDto articuloNuevo, Model modelo, RedirectAttributes mensajesRedireccion,  @RequestParam(name = "foto", required = false) MultipartFile foto) {
       try {
    	   
           byte[] fotoBytes = foto.getBytes();
           articuloNuevo.setFotoArticulo(fotoBytes);
           ArticuloDto articuloRegistrado = articuloServicio.registrarArticulo(articuloNuevo, fotoBytes);       
           
           log.info(articuloRegistrado.getNombre() + ", se ha registrado");    		   
           return "redirect:/admin/obtenerArticulos";
           
       }catch (CorreoExistenteExcepcion errorEmail) {
    	   mensajesRedireccion.addAttribute("mensajeErrorEmail", errorEmail.getMessage());
           return "redirect:/admin/obtenerArticulos";        
       }catch (IOException e) {
    	   mensajesRedireccion.addAttribute("mensajeError", "No pudimos procesar la foto.");
    	   return "redirect:/admin/obtenerArticulos";
       }
    }
}
