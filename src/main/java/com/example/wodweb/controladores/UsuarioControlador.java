package com.example.wodweb.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.UsuarioServicio;

@Controller
public class UsuarioControlador {

	
	private UsuarioServicio usuarioServicio = new UsuarioServicio();
	
	  @GetMapping("/admin/obtenerUsuario")
		public String obtenerUsuarios(Model model) {
			model.addAttribute("usuarios", usuarioServicio.obtenerUsuarios());
			return "usuarios";
		}
	  
	  @GetMapping("/registro")
	  public String mostrarFormularioRegistro() {
	      return "registro"; // Nombre del template de Thymeleaf: registro.html
	  }
	  
	  @PostMapping("/registroDatos")
	    public String registrarUsuario(UsuarioDto usuarioCredenciales, Model model, RedirectAttributes redirectAttributes) {
	       
		  UsuarioDto usuarioRegistrado = usuarioServicio.registrarUsuario(usuarioCredenciales);
	        if (usuarioRegistrado != null) {
	        	redirectAttributes.addFlashAttribute("mensaje", "Registro exitoso. ¡Bienvenido!");
	            model.addAttribute("mensaje", "Registro exitoso");
	            return "bienvenida"; // O a otra vista
	        } else {
	        	redirectAttributes.addFlashAttribute("mensaje", "Error en el registro. Inténtelo de nuevo.");
	            model.addAttribute("error", "Error en el registro");
	            return "registro"; // Regresa al formulario
	        }
	    }

}
