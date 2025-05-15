package com.example.wodweb.servicios;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.ArticuloDto;
import com.example.wodweb.dtos.UsuarioDto;

/**
 * Contiene la implementacion de los metodos de los articulos
 * msm - 130525
 */
@Service
public class ArticuloServicio {

	private  RestTemplate restTemplate;    
    private  String apiUrl;

    public ArticuloServicio() {
        this.restTemplate = new RestTemplate();
        this.apiUrl = "http://localhost:9511/articulo";
    }
    
    /**
     * Obtiene la lista de todos los articulos.
     * msm - 130525
     * @return Lista con todos los articulos.
     */
    public List<ArticuloDto> obtenerArticulos() {
        String url = apiUrl + "/mostrarArticulos";
        ArticuloDto[] articulo = restTemplate.getForObject(url, ArticuloDto[].class);
        return Arrays.asList(articulo);
    }
    
    /**
     * Elimina un articulo por su ID.
     * msm - 140525
     * @param id ID del articulo a eliminar.
     * @return true si el articulo fue eliminado con éxito, false en caso de error.
     */
    public boolean borrarArticulo(Long id) {
        try {
            String url = apiUrl + "/borrarArticulo/" + id;
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, null, UsuarioDto.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error al borrar usuario en la API externa: " + e.getMessage());
            return false;
        }
    }
    
    
    /**
     * Modifica un articulo existente.
     * msm - 150525
     * @param nombre Nombre del articulo a modificar.
     * @param campo Campo a actualizar.
     * @param nuevoValor Nuevo valor del campo.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean editarArticulo(String nombre, String campo, String nuevoValor) {
        String url = apiUrl + "/modificarArticulo?nombre=" + nombre + "&campo=" + campo + "&nuevoValor=" + nuevoValor;
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
        return response.getStatusCode() == HttpStatus.OK;
    }
    
    
}
