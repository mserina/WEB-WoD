package com.example.wodweb.controladores;

import java.io.IOException;
import java.security.Principal;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.excepciones.CorreoExistenteExcepcion;
import com.example.wodweb.excepciones.UsuarioNoEncontradoExcepcion;
import com.example.wodweb.servicios.UsuarioServicio;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para la gestión de usuarios.
 * msm - 050325
 */
@Controller
public class UsuarioControlador {
  
	@Autowired
	private UsuarioServicio usuarioServicio; //Inyeccion del servicio para usuarios
	private static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class); //Instancia de clase para generar logs
	Authentication credencialesSesion; //Variable que se usa para guardar los datos de una sesion
	String nombreUsuarioLog = "El usuario"; //Nombre que se usara en el log, en caso de no haber sesion de un usuario
    
    
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
    public String obtenerUsuarios(Model modelo) {
    	//Se guarda los datos de la sesion 
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();
    	modelo.addAttribute("usuarios", usuarioServicio.obtenerUsuarios());
        
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
        log.info(nombreUsuarioLog + " accedio a la lista de usuarios");
        return "usuarios";
    }

    
    
    /**
     * Método que muestra el formulario de registro.
     * msm - 050325
     * @return Devuelve la vista "registro".
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model modelo) {
    	
    	credencialesSesion = SecurityContextHolder.getContext().getAuthentication();

    	if (credencialesSesion != null && credencialesSesion.getPrincipal() instanceof SesionDto) {
            SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            String correo = sesion.getUsername();
            UsuarioDto u = usuarioServicio.buscarUsuario(correo);
            modelo.addAttribute("usuario", u);
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
    public String registrarUsuario(HttpSession sesion ,UsuarioDto usuarioCredenciales, Model model, RedirectAttributes redirectAttributes,  @RequestParam("archivoFoto") MultipartFile foto) {
       try {
    	   
    	   // Generar código de verificación
           String codigo = usuarioServicio.generarCodigo();
           usuarioCredenciales.setCodigoVerificado(codigo);
           
           byte[] fotoBytes = foto.getBytes();
           usuarioCredenciales.setFoto(fotoBytes);
           UsuarioDto usuarioRegistrado = usuarioServicio.registrarUsuario(usuarioCredenciales, fotoBytes);

           
           // Si el registro es correcto 
    	   if (usuarioRegistrado != null) {
               // Enviar el código por correo al usuario
               String asunto = "Código de verificación de registro";
               String mensaje = "Hola " + usuarioRegistrado.getNombreCompleto() + ",\n\n"
                               + "Tu código de verificación es: " + codigo + "\n\n"
                               + "Ingresa este código en la página de verificación para completar tu registro.";
          
               usuarioServicio.enviarCorreo(usuarioRegistrado.getCorreoElectronico(), asunto, mensaje);
    		   	
               
             // Guardar código y correo en la sesión
              sesion.setAttribute("codigoVerificacion", codigo);
              sesion.setAttribute("correoPendiente", usuarioRegistrado.getCorreoElectronico());
       
              
               log.info(usuarioRegistrado.getNombreCompleto() + ", se ha registrado");    		   
               return "verificarCodigo";
           } else {
               redirectAttributes.addFlashAttribute("mensaje", "Error en el registro. Inténtelo de nuevo.");
               return "registro"; // Regresa al formulario
           }
       }catch (CorreoExistenteExcepcion errorEmail) {
           model.addAttribute("mensajeErrorEmail", errorEmail.getMessage());
           return "registro";        
       }catch (IOException e) {
    	   model.addAttribute("mensajeError", "No pudimos procesar la foto.");
           return "registro";
       }
    }

    @GetMapping("/perfil")
    public String verPerfil(Model model, Principal principal) {
        // Asume que obtienes el email del principal y buscas al usuario
        String email = principal.getName();
    	UsuarioDto u = usuarioServicio.buscarUsuario(email);
        model.addAttribute("usuario", u);
        return "perfil"; // Thymeleaf template: perfil.html
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
    public String mostrarFormularioVerificacion(Model modelo, HttpSession session, RedirectAttributes mensajeFlash) {
    	// Si la sesión ya no tiene el código, entendemos que expiró
        if (session.getAttribute("codigoVerificacion") == null) {
        	SesionDto sesion = (SesionDto) credencialesSesion.getPrincipal();
            String correo = sesion.getUsername();
            UsuarioDto u = usuarioServicio.buscarUsuario(correo);
            modelo.addAttribute("usuario", u);
        	mensajeFlash.addFlashAttribute("mensajeErrorCodigo", "El tiempo de verificación ha expirado. Por favor, regístrate de nuevo.");
            // Puedes redirigir al registro, o a /verificarCodigo de nuevo:
            return "redirect:/registro";
        }
        return "verificarCodigo";
    }
    
    
    /**
     * Valida el codigo ingresado por el usuario
     * msm - 110425
     * @param codigoIngresado
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/verificarCodigo")
    public String verificarCodigo(@RequestParam String codigoIngresado,RedirectAttributes mensajeFlash, HttpSession sesion) {
    	
    	String codigoGuardado = (String) sesion.getAttribute("codigoVerificacion");
        String correoPendiente = (String) sesion.getAttribute("correoPendiente");
        
        if (codigoGuardado == null) {
            // Sesión expiró justo antes de enviar el POST
        	mensajeFlash.addFlashAttribute("mensajeErrorCodigo", "El tiempo de verificación ha expirado. Por favor, regístrate de nuevo.");
            return "redirect:/registro";
        }
        
        if (codigoGuardado.equals(codigoIngresado)) {
        	// Marcar usuario como verificado
            usuarioServicio.marcarUsuarioComoVerificado(correoPendiente);

            // Limpiar la sesión
            sesion.removeAttribute("codigoVerificacion");
            sesion.removeAttribute("correoPendiente");
        	
            usuarioServicio.marcarUsuarioComoVerificado(correoPendiente);
            
            mensajeFlash.addFlashAttribute( "mensajeLoginCodigo", "Código verificado. ¡Ya puedes iniciar sesión!");
            return "redirect:/login";
        } else {
        	mensajeFlash.addFlashAttribute("mensajeErrorCodigo", "Código incorrecto. Por favor, inténtelo de nuevo.");
            return "redirect:/verificarCodigo";
        }
    }
    
    @GetMapping("/reenviarCodigo")
    public String reenviarCodigo(HttpSession sesion, RedirectAttributes redirectAttributes) {
        
    	String correo = (String) sesion.getAttribute("correoPendiente");

        if (correo != null) {
            try {
                // Regenerar código y reenviarlo
                String nuevoCodigo = usuarioServicio.generarCodigo();
                sesion.setAttribute("codigoVerificacion", nuevoCodigo);

                String asunto = "Nuevo código de verificación";
                String mensaje = "Este es tu nuevo código de verificación: " + nuevoCodigo;

                usuarioServicio.enviarCorreo(correo, asunto, mensaje);
                redirectAttributes.addFlashAttribute("mensaje", "Se ha enviado un nuevo código a tu correo.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "No se pudo enviar el código. Intenta nuevamente.");
            }
        } else {
            redirectAttributes.addFlashAttribute("mensajeErrorCodigo", "No hay un registro pendiente. Por favor, regístrate de nuevo.");
            return "redirect:/registro";
        }

        return "redirect:/verificarCodigo";
    }
    
    
    // ------------- ¿OLVIDO CONTRASEÑA? ----------------------//
    
    /**
     * Dirige al usuario a la pagina para ingresar su correo
     * msm - 010525
     * @return La pagina de olvidasteContraseña
     */
    @GetMapping("/contrasenaOlvidada")
    public String olvideContrasena() {
        return "olvidasteContrasena";
    }
    
    /**
     * Se comprueba que existe el correo y se le manda al usuario un correo, ademas de crearle un token
     * msm - 010525
     * @param email correo ingresado por el usuario
     * @param mensajeInterfaz Mensajes que se imprimen en la pantalla
     * @return La pagina de contraseñaOlvidada (con mensaje de exito o error)
     */
    @PostMapping("/contrasenaOlvidada")
    public String envioIntrucciones(@RequestParam String email, RedirectAttributes mensajeInterfaz) {
        try {
            usuarioServicio.enviarTokenDeRecuperacion(email);
            mensajeInterfaz.addFlashAttribute("mensajeExito", "Se ha enviado un enlace de recuperación a tu correo.");
            return "redirect:/contrasenaOlvidada?success";
        } catch (UsuarioNoEncontradoExcepcion ci) {
        	mensajeInterfaz.addFlashAttribute("mensajeError", "No fue posible generar el enlace. Verifica tu correo.");
            return "redirect:/contrasenaOlvidada?error";
    	} catch (Exception ex) {
    		mensajeInterfaz.addFlashAttribute("mensajeError", "Se ha producido un error, intentelo mas tarde.");
	        return "redirect:/contrasenaOlvidada?error";
    	}
    }

    
    /**
     * Se comprueba el token antes de permitir el acceso al usuario
     * msm - 010525
     * @param token se extrae de la peticion
     * @param model 
     * @param mensajeInterfaz Mensajes que se imprimen en la pantalla
     * @return Devuelve la pagina reiniciarContrasena
     */
    @GetMapping("/reiniciarContrasena")
    public String showResetForm(@RequestParam String token, Model model, RedirectAttributes mensajeInterfaz) {
        try {
            // Validar el token 
            usuarioServicio.validarToken(token);
            model.addAttribute("token", token);
            return "resetearContrasena";
            
        } catch (IllegalArgumentException e) {
        	mensajeInterfaz.addFlashAttribute("mensajeError", "El token ha expirado");
            return "redirect:/login";
        }
    }
    
    /**
     * Cambia la contraseña por la nueva 
     * msm - 300425
     * @param token, se extrae de la peticion
     * @param contrasena, nueva contraseña que ingresa el usuario 
     * @param confirmaContrasena, contraseña que se vuelve a insertar
     * @param mensajeInterfaz Mensajes que se imprimen en la pantalla
     * @return la pagina de login si sale bien, vuelve a la pagina misma pagina en caso contrario
     */
    @PostMapping("/reiniciarContrasena")
    public String reinicioContraseña( @RequestParam String token, @RequestParam String contrasena, @RequestParam String confirmaContrasena, RedirectAttributes flash) {
        
    	//Se comprueba que la contraseña se ingreso correctamente en ambos campos
    	if (!contrasena.equals(confirmaContrasena)) {
            flash.addFlashAttribute("mensajeError", "Las contraseñas no coinciden.");
            return "redirect:/reset-password?token=" + token;
        }
        try {
            usuarioServicio.reiniciarContrasena(token, contrasena);
            log.info("Contraseña cambiada correctamente para token {}", token);
            flash.addFlashAttribute("mensajeExito", "Tu contraseña ha sido actualizada. Ya puedes iniciar sesión.");
            return "redirect:/login?resetSuccess";
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("mensajeError", e.getMessage());
            return "redirect:/reset-password?token=" + token;
        } catch (Exception e) {
            flash.addFlashAttribute("mensajeError", "Error interno al cambiar la contraseña.");
            return "redirect:/reset-password?token=" + token;
        }
    }
    
}
