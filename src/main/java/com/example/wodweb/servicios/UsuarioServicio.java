package com.example.wodweb.servicios;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.UsuarioDto;

/**
 * Servicio que se encarga de la comunicación con la API externa de productos
 */
@Service
public class UsuarioServicio {
	   
	  private RestTemplate restTemplate;    
	  private String apiUrl;

	  public UsuarioServicio() {
	      this.restTemplate = new RestTemplate();
	      this.apiUrl = "http://localhost:9511/usuario";
	  }
	    
	 
	
	 
	 /**
	  * Meotodo para mostrar todo los usuarios
	  * msm - 010325
	  * @return Devuelve una lista con todo los usuarios
	  */
	 public List<UsuarioDto> obtenerUsuarios() {
	        String url = apiUrl + "/mostrarUsuarios";  // Asegúrate de que la API tenga este endpoint
	        UsuarioDto[] usuarios = restTemplate.getForObject(url, UsuarioDto[].class);
	        return Arrays.asList(usuarios); // Convertir array a lista
	 }
	 
	 public UsuarioDto registrarUsuario(UsuarioDto usuarioCredenciales) {
	        try {
	        	
	        	//Definimos el formato en el que se mandaran los datos a la API en la cabezera
	        	HttpHeaders headers = new HttpHeaders();
		        headers.set("Content-Type", "application/json");
	            
		        // Construimos la peticion con la cabezera y los datos recopilados en el formulario
	            HttpEntity<UsuarioDto> requestEntity = new HttpEntity<>(usuarioCredenciales, headers);
	            
	            // Suponiendo que la API externa expone un endpoint para registrar usuarios, por ejemplo: /register
	            ResponseEntity<UsuarioDto> response = restTemplate.postForEntity(apiUrl + "/crear", requestEntity, UsuarioDto.class);
	            
	            return response.getBody();
	        } catch (Exception e) {
	            // Manejar errores, por ejemplo, loggear y devolver null o lanzar una excepción personalizada
	            System.err.println("Error al registrar usuario en la API externa: " + e.getMessage());
	            return null;
	        }
	    }
	
    
}
