package com.example.wodweb.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  
    private UsuarioServicio usuarioServicio = new UsuarioServicio();
    
	private static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class);

    
    
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
    	log.info("El administrador accedio a la lista de usuarios");
        model.addAttribute("usuarios", usuarioServicio.obtenerUsuarios());
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof SesionDto) {
            SesionDto usuario = (SesionDto) authentication.getPrincipal();
            model.addAttribute("auth", usuario); // Enviar usuario autenticado a la vista
        }
        return "usuarios";
    }

    
    
    /**
     * Método que muestra el formulario de registro.
     * msm - 050325
     * @return Devuelve la vista "registro".
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro() {
    	log.info("El usuario accedio al registro");
        return "registro"; // Nombre del template de Thymeleaf: registro.html
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
       log.info("El usuario accedio al registro");
       try {
           UsuarioDto usuarioRegistrado = usuarioServicio.registrarUsuario(usuarioCredenciales);

    	   if (usuarioRegistrado != null) {
               redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. ¡Bienvenido!");
               return "bienvenida"; // O redirigir a otra vista
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

        if (actualizado) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado con éxito.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar usuario.");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }

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
        boolean borrado = usuarioServicio.borrarUsuario(id);

        if (borrado) {
            redirectAttributes.addFlashAttribute("mensaje", "Usuario borrado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("mensaje", "Error al borrar el usuario.");
        }

        return "redirect:/admin/obtenerUsuario"; // Redirige a la vista con la lista de usuarios
    }
}
