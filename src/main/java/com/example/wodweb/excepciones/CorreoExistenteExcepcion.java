package com.example.wodweb.excepciones;

public class CorreoExistenteExcepcion extends RuntimeException {
    
	public CorreoExistenteExcepcion(String message) {
        super(message);
    }
}
