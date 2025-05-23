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

import jakarta.servlet.http.HttpSession;

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
	private static final Logger log = LoggerFactory.getLogger("logMensajes"); //Instancia de clase para generar logs
	Authentication credencialesSesion; //Variable que se usa para guardar los datos de una sesion
	String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
	@Autowired
	private Environment env;
	
	
	
	
	@GetMapping("/catalogo/manga")
	public String catalogoManga(Model modelo) {
	    List<ArticuloDto> articulos = articuloServicio.obtenerPorTipo("manga");

	    String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	    }

	    modelo.addAttribute("articulosLista", articulos);
	    modelo.addAttribute("titulo", "Manga");

	    log.info(nombreUsuarioLog + " accedió al catalogo de manga");
	    return "catalogo";
	}

	
	@GetMapping("/catalogo/figura")
	public String catalogoFigura(Model modelo) {
	    List<ArticuloDto> articulos = articuloServicio.obtenerPorTipo("figura");
	 // 1) Perfil activo (si no hay ninguno, "default")
	    String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	    }

	    modelo.addAttribute("titulo", "Figura");
        modelo.addAttribute("articulosLista", articulos); // ← Aquí se inyecta al modelo
        
        log.info(nombreUsuarioLog + " accedió al catalogo de figura");
	    return "catalogo";
	}
	
	@GetMapping("/catalogo/poster")
	public String catalogoPoster(Model modelo) {
	    List<ArticuloDto> articulos = articuloServicio.obtenerPorTipo("poster");
	 // 1) Perfil activo (si no hay ninguno, "default")
	    String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
	    modelo.addAttribute("perfilActivo", perfil);
	    
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.getPrincipal() instanceof SesionDto) {
	        SesionDto sesion = (SesionDto) auth.getPrincipal();
	        modelo.addAttribute("usuario", usuarioServicio.buscarUsuario(sesion.getUsername()));
	    }

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
    	//Se guarda los datos de la sesion 
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
    	modelo.addAttribute("articulos", articuloServicio.obtenerArticulos());
        
    	//Se comprueba que la sesion no sea null
        if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
        	// 1) Perfil activo (si no hay ninguno, "default")
    	    String perfil = env.getActiveProfiles().length > 0
    	                    ? env.getActiveProfiles()[0]
    	                    : "default";
    	    modelo.addAttribute("perfilActivo", perfil);
        	
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
    
    
    /**
     * Método para editar un articulo.
     * msm - 050325
     * @param nombre nombre del usuario a modificar.
     * @param campo Campo a actualizar.
     * @param nuevoValor Nuevo valor a asignar.
     * @param redirectAttributes Permite redirigir con mensajes flash.
     * @return Redirige a la vista con la lista de articulos.
     */
    @PostMapping("/admin/editarArticulo")
    public String editarArticulo(@RequestParam String nombre, @RequestParam String campo, @RequestParam(required = false) String nuevoValor,@RequestParam(name = "foto", required = false) MultipartFile foto,RedirectAttributes redirectAttributes) {
        
    	boolean actualizado;
        if ("foto".equals(campo) && foto != null && !foto.isEmpty()) {
            // Llamamos a la versión del servicio que recibe el archivo
            actualizado = articuloServicio.editarArticulo(nombre, campo, null, foto);
        } else {
            // Llamamos a la versión que sólo recibe texto
            actualizado = articuloServicio.editarArticulo(nombre, campo, nuevoValor, null);
        }

        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "Artículo actualizado con éxito.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar artículo.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        log.info("Se modificó el campo " + campo + " del artículo " + nombre);
        return "redirect:/admin/obtenerArticulos";
    }
    
    
    /**
     * Borra un usuario de la base de datos utilizando su ID.
     *
     * @param id Identificador único del usuario.
     * @return ResponseEntity con mensaje de éxito o error si el usuario no existe.
     */
    @PostMapping("/admin/borrarArticulo")
    public String borrarUsuario(@RequestParam Long id, RedirectAttributes redirectAttributes) {
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
    	String nombreArticuloBorrado = "";
    	
    	boolean borrado = articuloServicio.borrarArticulo(id); // Llamar al servicio para eliminar el articulo
        log.info(nombreUsuarioLog + " elimino el articulo " + nombreArticuloBorrado);
        
        	
        	if (borrado) {
                redirectAttributes.addFlashAttribute("mensaje", "Articulo borrado correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Error al borrar el articulo.");
            }
        
        return "redirect:/admin/obtenerArticulos";
    }
    
    @PostMapping("/admin/crearArticulo")
    public String registrarArticulo(HttpSession sesion ,ArticuloDto articuloNuevo, Model modelo, RedirectAttributes redirectAttributes,  @RequestParam(name = "foto", required = false) MultipartFile foto) {
       try {
    	   
           byte[] fotoBytes = foto.getBytes();
           articuloNuevo.setFotoArticulo(fotoBytes);
           ArticuloDto articuloRegistrado = articuloServicio.registrarArticulo(articuloNuevo, fotoBytes);       
           
           log.info(articuloRegistrado.getNombre() + ", se ha registrado");    		   
           return "redirect:/admin/obtenerArticulos";
           
       }catch (CorreoExistenteExcepcion errorEmail) {
    	   redirectAttributes.addAttribute("mensajeErrorEmail", errorEmail.getMessage());
           return "redirect:/admin/obtenerArticulos";        
       }catch (IOException e) {
    	   redirectAttributes.addAttribute("mensajeError", "No pudimos procesar la foto.");
    	   return "redirect:/admin/obtenerArticulos";
       }
    }
}
