package com.example.wodweb.servicios;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.excepciones.CorreoExistenteExcepcion;

/**
 * Servicio que contiene la lógica de las funcionalidades de los usuarios.
 * msm - 050325
 */
@Service
public class UsuarioServicio {
 
    private  RestTemplate restTemplate;    
    private  String apiUrl;

    public UsuarioServicio() {
        this.restTemplate = new RestTemplate();
        this.apiUrl = "http://localhost:9511/usuario";
    }

     
    /* /////////////////////////////////// */
    /*             METODOS                  */
    /* //////////////////////////////////// */

    
    
    /**
     * Obtiene la lista de todos los usuarios.
     * msm - 010325
     * @return Lista con todos los usuarios.
     */
    public List<UsuarioDto> obtenerUsuarios() {
        String url = apiUrl + "/mostrarUsuarios";
        UsuarioDto[] usuarios = restTemplate.getForObject(url, UsuarioDto[].class);
        return Arrays.asList(usuarios);
    }

    
    
    /**
     * Registra un nuevo usuario.
     * msm - 050325
     * @param usuarioCredenciales Datos del usuario a registrar.
     * @return Usuario registrado o null si ocurre un error.
     */
    public UsuarioDto registrarUsuario(UsuarioDto usuarioCredenciales) {
    	
    	// Obtener todos los usuarios registrados
        List<UsuarioDto> usuarios = obtenerUsuarios();
        
        // Comprobar si ya existe un usuario con el mismo correo (ignorando mayúsculas/minúsculas)
        boolean correoEnUso = usuarios.stream()
            .anyMatch(u -> u.getCorreoElectronico().equalsIgnoreCase(usuarioCredenciales.getCorreoElectronico()));
        
        if (correoEnUso) {
            // Puedes lanzar una excepción personalizada para que el controlador la maneje y envíe un mensaje al front
            throw new CorreoExistenteExcepcion("El correo ya está en uso.");
            // O alternativamente, retornar null o un objeto especial que indique el error.
        }
    	try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<UsuarioDto> requestEntity = new HttpEntity<>(usuarioCredenciales, headers);
            ResponseEntity<UsuarioDto> response = restTemplate.postForEntity(apiUrl + "/crear", requestEntity, UsuarioDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al registrar usuario en la API externa: " + e.getMessage());
            return null;
        }
    }

    
    /**
     * Modifica un usuario existente.
     * msm - 050325
     * @param correoElectronico Correo del usuario a modificar.
     * @param campo Campo a actualizar.
     * @param nuevoValor Nuevo valor del campo.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean editarUsuario(String correoElectronico, String campo, String nuevoValor) {
        String url = apiUrl + "/modificarUsuarios?correoElectronico=" + correoElectronico + "&campo=" + campo + "&nuevoValor=" + nuevoValor;
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
        return response.getStatusCode() == HttpStatus.OK;
    }

    
    
    /**
     * Elimina un usuario por su ID.
     * msm - 050325
     * @param id ID del usuario a eliminar.
     * @return true si el usuario fue eliminado con éxito, false en caso de error.
     */
    public boolean borrarUsuario(Long id) {
        try {
            String url = apiUrl + "/borrar/" + id;
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, null, UsuarioDto.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error al borrar usuario en la API externa: " + e.getMessage());
            return false;
        }
    }
}
