package com.example.wodweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.wodweb.controladores.InicioSesionControlador;

/**
 * Clase principal de la aplicacion
 * msm - 050324
 * @param args
 */
@SpringBootApplication
public class WodwebApplication {

	/**
	 * Metodo main, encargada de inicializar la aplicacion
	 * msm - 050324
	 * @param args
	 */
    public static void main(String[] args) {
        SpringApplication.run(WodwebApplication.class, args);
  
    }
}
