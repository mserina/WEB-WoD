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

/**
 * Servicio para la autenticación de usuarios.
 * Se encarga de la comunicación con la API externa para validar credenciales.
 * 
 * Autor: msm - 060325
 */
@Service
public class InicioSesionServicio {
    
    /*
     * ||| VARIABLES |||
     */
    private RestTemplate restTemplate = new RestTemplate();
    private String apiBaseUrl = "http://localhost:9511/usuario"; // URL de la API externa

    
    
    
    /*
     * |||| MÉTODOS ||||
     */
    
    /**
     * Método para autenticar a un usuario enviando sus credenciales a la API.
     * 
     * @param credencialesUsuario DTO con correo electrónico y contraseña del usuario.
     * @return Un objeto UsuarioDto si la autenticación es exitosa, de lo contrario, retorna null.
     */
    public UsuarioDto autenticarUsuario(InicioSesionDto credencialesUsuario) {
        
        System.out.println("apiBaseUrl = " + apiBaseUrl); // Verificación de la URL de la API

        String email = credencialesUsuario.getCorreoElectronico();
        String contrasena = credencialesUsuario.getContrasena();

        // Validación del formato del email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.err.println("Error: El email no tiene un formato válido.");
            return null;
        }

        // Validación de la longitud de la contraseña (mínimo 8 caracteres)
        if (contrasena == null || contrasena.length() < 8) {
            System.err.println("Error: La contraseña debe tener al menos 8 caracteres.");
            return null;
        }

        try {
            // Configuración de los encabezados de la petición
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // Creación de la entidad HTTP con las credenciales y los encabezados
            HttpEntity<InicioSesionDto> requestEntity = new HttpEntity<>(credencialesUsuario, headers);

            // Envío de la petición POST a la API externa
            ResponseEntity<UsuarioDto> response = restTemplate.exchange(
                apiBaseUrl + "/login",
                HttpMethod.POST,
                requestEntity,
                UsuarioDto.class
            );

            // Verificar si la respuesta contiene un cuerpo válido
            return response.getBody() != null ? response.getBody() : null;
        
        } catch (HttpClientErrorException e) {
            // Manejo de errores HTTP
            System.err.println("Error en la autenticación: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            // Manejo de errores de conexión
            System.err.println("No se pudo conectar con la API externa: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de cualquier otro error inesperado
            System.err.println("Error inesperado en la autenticación: " + e.getMessage());
        }
        
        return null; // Retorna null en caso de error
    }
}
