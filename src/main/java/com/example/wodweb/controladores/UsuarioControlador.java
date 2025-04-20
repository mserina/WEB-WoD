package com.example.wodweb.controladores;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.excepciones.CorreoExistenteExcepcion;
import com.example.wodweb.servicios.UsuarioServicio;

/**
 * Controlador para la gestión de usuarios.
 * msm - 050325
 */
@Controller
public class UsuarioControlador {
  
	@Autowired
	private UsuarioServicio usuarioServicio;
	private static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class);
	Authentication credencialesSesion;
	String nombreUsuarioLog = "El usuario";
    
    
    /* /////////////////////////////////// */
    /*             METODOS                  */
    /* //////////////////////////////////// */
    
    /**
     * Método para obtener la lista de usuarios y enviarla a la vista.
     * msm - 050325
     * @param model Modelo para pasar los datos a la vista.
     * @return Devuelve la vista "usuarios".
     */
    @GetMapping("/admin/obtenerUsuario")
    public String obtenerUsuarios(Model model) {
    	
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("usuarios", usuarioServicio.obtenerUsuarios());
        
        if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            // Usamos el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
            model.addAttribute("auth", sesion); // Enviar usuario autenticado a la vista
        }
        else {
    		nombreUsuarioLog = "El usuario";
    	}
        log.info(nombreUsuarioLog + " accedio a la lista de usuarios");
        return "usuarios";
    }

    
    
    /**
     * Método que muestra el formulario de registro.
     * msm - 050325
     * @return Devuelve la vista "registro".
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
    	
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();

    	if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            // Usamos el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
        }
    	else {
    		nombreUsuarioLog = "El usuario";
    	}
        log.info(nombreUsuarioLog + " accedio a registro");
        return "registro"; 
    }

    
    
    /**
     * Método que registra un usuario con los datos proporcionados.
     * msm - 050325
     * @param usuarioCredenciales Objeto con los datos del usuario.
     * @param model Modelo para pasar mensajes a la vista.
     * @param redirectAttributes Permite redirigir con mensajes flash.
     * @return Redirige a la vista de bienvenida si el registro es exitoso, de lo contrario, vuelve a "registro".
     */
    @PostMapping("/registroDatos")
    public String registrarUsuario(UsuarioDto usuarioCredenciales, Model model, RedirectAttributes redirectAttributes) {
       try {
    	   
    	   // Generar código de verificación
           String codigo = usuarioServicio.generarCodigo();
           
           usuarioCredenciales.setCodigoVerificacion(codigo);
           
           UsuarioDto usuarioRegistrado = usuarioServicio.registrarUsuario(usuarioCredenciales);

           // Si el registro es correcto 
    	   if (usuarioRegistrado != null) {
           	   
               // Enviar el código por correo al usuario
               String asunto = "Código de verificación de registro";
               String mensaje = "Hola " + usuarioRegistrado.getNombreCompleto() + ",\n\n"
                               + "Tu código de verificación es: " + codigo + "\n\n"
                               + "Ingresa este código en la página de verificación para completar tu registro.";
          
               usuarioServicio.enviarCorreo(usuarioRegistrado.getCorreoElectronico(), asunto, mensaje);
    		   	
               log.info(usuarioRegistrado.getNombreCompleto() + ", se ha registrado");    		   
               return "verificarCodigo";
           } else {
               redirectAttributes.addFlashAttribute("mensaje", "Error en el registro. Inténtelo de nuevo.");
               return "registro"; // Regresa al formulario
           }
       }catch (CorreoExistenteExcepcion errorEmail) {
           model.addAttribute("mensajeErrorEmail", errorEmail.getMessage());
           return "registro";        
       }
    }

    
    
    /**
     * Método para editar un usuario.
     * msm - 050325
     * @param correoElectronico Correo del usuario a modificar.
     * @param campo Campo a actualizar.
     * @param nuevoValor Nuevo valor a asignar.
     * @param redirectAttributes Permite redirigir con mensajes flash.
     * @return Redirige a la vista con la lista de usuarios.
     */
    @PostMapping("/admin/editarUsuario")
    public String editarUsuario(@RequestParam String correoElectronico, @RequestParam String campo, @RequestParam String nuevoValor, RedirectAttributes redirectAttributes) {
        boolean actualizado = usuarioServicio.editarUsuario(correoElectronico, campo, nuevoValor);
        credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuarioModificado = "";
        
        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado con éxito.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar usuario.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        

    	if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            // Usamos el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
        }
    	else {
    		nombreUsuarioLog = "El usuario";
    	}

    	List<UsuarioDto> usuarios = usuarioServicio.obtenerUsuarios();
    	for (UsuarioDto usuario : usuarios) {
    		if(usuario.getCorreoElectronico().equals(correoElectronico)) {
    			nombreUsuarioModificado = usuario.getNombreCompleto();
    		}
    	}
    	log.info(nombreUsuarioLog + " modifico el campo " + campo + " del usuario " + nombreUsuarioModificado);
        return "redirect:/admin/obtenerUsuario"; // Redirigir de nuevo a la lista de usuarios
    }

    
    
    /**
     * Método para eliminar un usuario.
     * msm - 050325
     * @param id ID del usuario a eliminar.
     * @param redirectAttributes Permite redirigir con mensajes flash.
     * @return Redirige a la vista con la lista de usuarios después de eliminar.
     */
    @PostMapping("/admin/borrarUsuario")
    public String borrarUsuario(@RequestParam Long id, RedirectAttributes redirectAttributes) {

    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuarioBorrado = "";
        
        List<UsuarioDto> usuarios = usuarioServicio.obtenerUsuarios();
    	for (UsuarioDto usuario : usuarios) {
    		if(usuario.getId() == id) {
    			nombreUsuarioBorrado = usuario.getNombreCompleto();
    		}
    	}
    	
    	log.info(nombreUsuarioLog + " elimino al usuario " + nombreUsuarioBorrado);
        boolean borrado = usuarioServicio.borrarUsuario(id);

        if (borrado) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario borrado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al borrar el usuario.");
        }
        
        if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            // Usamos el nombre del usuario de sesión
            nombreUsuarioLog = sesion.getNombre();
        }
    	else {
    		nombreUsuarioLog = "El usuario";
    	}

        
        return "redirect:/admin/obtenerUsuario"; // Redirige a la vista con la lista de usuarios
    }
    
    
    /**
     * Muestra la pagina para insertar el codigo de verificacion
     * msm - 110425 
     * @param model
     * @return
     */
    @GetMapping("/verificarCodigo")
    public String mostrarFormularioVerificacion(Model model) {
        return "verificarCodigo";  // El nombre del template Thymeleaf
    }
    
    /**
     * Valida el codigo ingresado por el usuario
     * msm - 110425
     * @param codigoIngresado
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/verificarCodigo")
    public String verificarCodigo(@RequestParam String codigoIngresado, RedirectAttributes redirectAttributes) {
   
        boolean verificado = usuarioServicio.verificarCodigo(codigoIngresado);     
        
        if (verificado) {
            redirectAttributes.addFlashAttribute("mensaje", "Código verificado. ¡Registro completado!");
            return "redirect:/registroDatos"; // Redirige a la página de bienvenida o lo que corresponda
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Código incorrecto. Por favor, inténtelo de nuevo.");
            return "redirect:/verificarCodigo";
        }
    }
}
