package com.example.wodweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.wodweb.controladores.InicioSesionControlador;

@SpringBootApplication
public class WodwebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WodwebApplication.class, args);
  
		/*
		 * InicioSesionControlador inicioSesion = new InicioSesionControlador();
		 * inicioSesion.autenticarUsuarios();
		 */
		 
    }
}
