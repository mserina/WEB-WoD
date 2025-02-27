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
import com.example.wodweb.dtos.UsuarioDto;

@Service
public class InicioSesionServicio {
       
	private RestTemplate restTemplate = new RestTemplate();
	private String apiBaseUrl = "http://localhost:9511/usuario"; // URL de la API externa

	
	
	public UsuarioDto autenticarUsuario(InicioSesionDto credencialesUsuario) {
	    System.out.println("apiBaseUrl = " + apiBaseUrl); // Verifica si está inyectado

	    String email = credencialesUsuario.getCorreoElectronico();
	    String contrasena = credencialesUsuario.getContrasena();

	    // Validación del formato de email
	    if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
	        System.err.println("Error: El email no tiene un formato válido.");
	        return null;
	    }

	    // Validación de la longitud de la contraseña (y comprobación nula)
	    if (contrasena == null || contrasena.length() < 8) {
	        System.err.println("Error: La contraseña debe tener al menos 8 caracteres.");
	        return null;
	    }

	    try {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Content-Type", "application/json");

	        HttpEntity<InicioSesionDto> requestEntity = new HttpEntity<>(credencialesUsuario, headers);
	        ResponseEntity<UsuarioDto> response = restTemplate.exchange(
	            apiBaseUrl + "/login",
	            HttpMethod.POST,
	            requestEntity,
	            UsuarioDto.class
	        );

	        // Verificar si la respuesta contiene un cuerpo válido
	        if (response.getBody() != null) {
	            return response.getBody();
	        } else {
	            return null;
	        }
	    } catch (HttpClientErrorException e) {
	        System.err.println("Error en la autenticación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
	    } catch (ResourceAccessException e) {
	        System.err.println("No se pudo conectar con la API externa: " + e.getMessage());
	    } catch (Exception e) {
	        System.err.println("Error inesperado en la autenticación: " + e.getMessage());
	    }
	    
	    return null; // Devuelve null en caso de error
	}

}