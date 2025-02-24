package com.example.wodweb.servicios;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.InicioSesionDto;

@Service
public class InicioSesionServicio {
       
	private RestTemplate restTemplate = new RestTemplate();
	private String apiBaseUrl = "http://localhost:9511/usuario"; // URL de la API externa

	
	
	public Boolean autenticarUsuario(InicioSesionDto credencialesUsuario) {
	    System.out.println("apiBaseUrl = " + apiBaseUrl); // Verifica si está inyectado

	   

	    String email = credencialesUsuario.getCorreoElectronico();
	    String contrasena = credencialesUsuario.getContrasena();

	    
	    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	        System.err.println("Error: El email no tiene un formato válido.");
	        return false;
	    }

	    
	    if (contrasena.length() < 8) {
	        System.err.println("Error: La contraseña debe tener al menos 8 caracteres.");
	        return false;
	    }

	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Type", "application/json");

	        HttpEntity<InicioSesionDto> requestEntity = new HttpEntity<>(credencialesUsuario, headers);
	        ResponseEntity<Boolean> response = restTemplate.exchange(
	            apiBaseUrl + "/login",
	            HttpMethod.POST,
	            requestEntity,
	            Boolean.class
	        );

	        // ✅ Verificar si la respuesta tiene cuerpo antes de retornarlo
	        if (response.getBody() != null) {
	            return response.getBody();
	        } else {
	            System.err.println("Error: La respuesta no contiene un cuerpo válido.");
	        }

	    } catch (HttpClientErrorException e) {
	        // Captura errores HTTP como 400, 401, 403, etc.
	        System.err.println("Error en la autenticación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
	    } catch (ResourceAccessException e) {
	        // Captura errores de conexión con la API
	        System.err.println("No se pudo conectar con la API externa: " + e.getMessage());
	    } catch (Exception e) {
	        // Captura cualquier otra excepción
	        System.err.println("Error inesperado en la autenticación: " + e.getMessage());
	    }
	    
	    return false; // Devuelve false en caso de error
	}
}