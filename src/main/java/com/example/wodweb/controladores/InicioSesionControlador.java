package com.example.wodweb.controladores;

import java.util.Scanner;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.wodweb.dtos.InicioSesionDto;
import com.example.wodweb.dtos.SesionDto;
import com.example.wodweb.servicios.InicioSesionServicio;


@Controller
public class InicioSesionControlador {
	

	/*
	 * @PostMapping("/login") public void bienvenida(Model model) {
	 * 
	 * }
	 */
	
    private InicioSesionServicio inicioSesionServicio = new InicioSesionServicio();
	
    private Scanner sc = new Scanner(System.in);
    private InicioSesionDto credencialesUsuario = new InicioSesionDto();
    private SesionDto sesion = new SesionDto();
    private boolean acceso = true;
	
	 @PostMapping("/login")
	 public void autenticarUsuarios() {
		 
		 System.out.println(" ");
		 System.out.println("Ingrese email");
		 String email = sc.next();
		 System.out.println("Ingrese contrasena");
		 String contrasena = sc.next();
		 
		 credencialesUsuario.setCorreoElectronico(email);
		 credencialesUsuario.setContrasena(contrasena);
		 
		 acceso = inicioSesionServicio.autenticarUsuario(credencialesUsuario);
		 System.out.println(acceso);
		 
		  // Llamar al servicio de autenticación
			/*
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
			 */
		 

	 }
	
	 // cabezera para sacar la respuesta en la vista
	 //public ResponseEntity<SesionDto> autenticarUsuarios() {
 
}
