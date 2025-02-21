package com.example.wodweb.servicios;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.InicioSesionDto;
import com.example.wodweb.dtos.SesionDto;

@Service
public class InicioSesionServicio {
       
	private RestTemplate restTemplate = new RestTemplate();
	
	@Value("${api.base-url}")
	private String apiBaseUrl = "http://localhost:9511/usuario"; // URL de la API externa

	public Boolean autenticarUsuario(InicioSesionDto credencialesUsuario) {
		 System.out.println("apiBaseUrl = " + apiBaseUrl); // Verifica si está inyectado
		 System.out.println(" "); 
		 System.out.println(credencialesUsuario.getCorreoElectronico()); // Verifica si está inyectado

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
            
            // Verificar si la respuesta tiene cuerpo antes de retornarlo
            if (response.getBody() != null) {
                return response.getBody();
            } else {
                System.err.println("La respuesta no contiene un cuerpo válido.");
            }

        } catch (HttpClientErrorException e) {
            // Captura errores HTTP como 400, 401, 403, etc.
            System.out.println(" ");
            System.err.println("Error en la autenticación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            // Captura errores de conexión con la API
        	System.out.println(" ");
            System.err.println("No se pudo conectar con la API externa: " + e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otra excepción
        	System.out.println(" ");
            System.err.println("Error inesperado en la autenticación: " + e.getMessage());
        }
        return null; // Devuelve null si ocurre un error
    }
}