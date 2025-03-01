package com.example.wodweb.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.servicios.ProductoServicio;

/**
 * Clase controlador, contiene los endpoints por los que se acceden msm - 040224
 */
@Controller
public class BienvenidaController {

	// Servicio que se encarga de obtener los productos de la API
	private ProductoServicio productoServicio;

	/**
	 * Constructor que recibe el servicio de productos mediante inyección de
	 * dependencias
	 * 
	 * @param productoServicio Servicio para obtener los productos
	 */
	@Autowired
	public BienvenidaController(ProductoServicio productoServicio) {
		this.productoServicio = productoServicio;
	}

	/**
	 * Maneja la página principal de bienvenida URL: http://localhost:8080/
	 * 
	 * @param model Modelo para pasar datos a la vista
	 * @return Nombre del html de bienvenida
	 */
	@GetMapping("/")
	public String bienvenida(Model modelo) {
		modelo.addAttribute("mensaje", "¡Bienvenido a nuestra tienda de manga!");
		return "bienvenida";

	}

	/**
	 * Maneja la página del catálogo de productos URL:
	 * http://localhost:8080/productos
	 * 
	 * @param model Modelo para pasar datos a la vista
	 * @return Nombre del html de productos
	 */
	@GetMapping("/productos")
	public String productos(Model model) {
		List<UsuarioDto> productos = productoServicio.obtenerTodosLosProductos();

		if (productos.isEmpty()) {
			model.addAttribute("mensaje", "No hay productos disponibles en este momento");
		} else {
			model.addAttribute("mensaje", "Nuestro catálogo de manga");
		}

		model.addAttribute("productos", productos);
		return "productos";
	}

	/*
	*//**
		 * Metodo para verificar conexion con la API msm - 030225
		 * 
		 * @param model Los datos que se pasan a la vista
		 * @return La plantilla HTML
		 *//*
			 * @GetMapping("/verificar") public String verificarConexion(Model model) {
			 * String respuesta = productoServicio.verificarConexion();
			 * model.addAttribute("mensaje", respuesta); return "resultado"; // Nombre de la
			 * vista HTML }
			 */

	@GetMapping("/admin/bienvenido")
	public String bienvenidaAdmin() {
		return "admin";
	}
}
