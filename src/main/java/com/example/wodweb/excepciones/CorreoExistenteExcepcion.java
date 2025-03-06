package com.example.wodweb.excepciones;

/**
 * Contiene una excepcion personalizada para la comprobacion de correo (login)
 * msm - 060325
 */
public class CorreoExistenteExcepcion extends RuntimeException {
    
	/**
	 * Contiene el mensaje que se le devolvera al usuario cuando se lanze la excepcion
	 * msm - 060325
	 * @param mensaje El mensaje que recoge al lanzar la excepcion
	 */
	public CorreoExistenteExcepcion(String mensaje) {
        super(mensaje);
    }
}
