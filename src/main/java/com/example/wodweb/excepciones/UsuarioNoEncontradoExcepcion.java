package com.example.wodweb.excepciones;

/**
 * Excepcion personalizada cuando no se encuentar un correo en la funcion "Olvide mi contraseña"
 * msm - 280425 
 */
public class UsuarioNoEncontradoExcepcion extends RuntimeException {
    
	/**
	 * Contiene el mensaje que se le devolvera al usuario cuando se lanze la excepcion
	 * msm - 060325
	 * @param mensaje El mensaje que recoge al lanzar la excepcion
	 */
	public UsuarioNoEncontradoExcepcion(String mensaje) {
        super(mensaje);
    }
}
