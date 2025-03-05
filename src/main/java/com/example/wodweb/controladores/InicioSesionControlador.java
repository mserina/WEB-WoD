package com.example.wodweb.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.wodweb.dtos.InicioSesionDto;
import com.example.wodweb.servicios.InicioSesionServicio;

@Controller
public class InicioSesionControlador {

	private InicioSesionDto credencialesUsuario = new InicioSesionDto();
	private boolean acceso = true;
	private InicioSesionServicio inicioSesionServicio = new InicioSesionServicio();
	
	
	@GetMapping("/login")
	public String login(Model model, @RequestParam(required = false) String logout) {
        if (logout != null) {
            model.addAttribute("logoutMessage", "Has cerrado sesión exitosamente.");
        }
        return "login"; // Asegúrate de que el nombre del archivo HTML de login sea correcto
    }
	
	

	
	//   INICIO SESION CON CONSOLA
	/*
	 * @PostMapping("/login") public void autenticarUsuarios() {
	 * 
	 * System.out.println(" "); System.out.println("Ingrese email"); String email =
	 * sc.next(); System.out.println("Ingrese contrasena"); String contrasena =
	 * sc.next();
	 * 
	 * credencialesUsuario.setCorreoElectronico(email);
	 * credencialesUsuario.setContrasena(contrasena);
	 * 
	 * acceso = inicioSesionServicio.autenticarUsuario(credencialesUsuario);
	 * System.out.println(acceso);
	 * 
	 * // Llamar al servicio de autenticación
	 * 
	 * SesionDto sesion =
	 * inicioSesionServicio.autenticarUsuario(credencialesUsuario);
	 * 
	 * // Mostrar la respuesta if (sesion != null) { System.out.println(" ");
	 * System.out.println("Inicio de sesión exitoso.");
	 * System.out.println("ID Usuario: " + sesion.getIdUsuario());
	 * System.out.println("Rol: " + sesion.getRol()); } else {
	 * System.out.println(" ");
	 * System.out.println("Error en la autenticación. Verifique sus credenciales.");
	 * }
	 * 
	 * 
	 * }
	 */

	// cabezera para sacar la respuesta en la vista
	// public ResponseEntity<SesionDto> autenticarUsuarios() {

}
