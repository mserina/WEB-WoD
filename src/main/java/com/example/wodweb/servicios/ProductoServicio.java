package com.example.wodweb.servicios;

import java.util.Arrays;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.UsuarioDto;

/**
 * Servicio que se encarga de la comunicación con la API externa de productos
 */
@Service
public class ProductoServicio {

    private final RestTemplate restTemplate;
    
    private final String apiUrl;

    public ProductoServicio() {
        this.restTemplate = new RestTemplate();
        this.apiUrl = "http://192.192.192:8080/api/productos";
    }

    /**
     * Obtiene todos los productos de la API externa
     */
    public List<UsuarioDto> obtenerTodosLosProductos() {
        try {
            UsuarioDto[] productos = restTemplate.getForObject(apiUrl, UsuarioDto[].class);
            
            if (productos == null) {
                return Collections.emptyList();
            }
            
            return Arrays.asList(productos);
            
        } catch (Exception e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    
    
    @Value("${api.base-url}")
    private String apiBaseUrl;

    public String verificarConexion() {
    	System.out.println(" ");
    	System.out.println(apiBaseUrl);
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(apiBaseUrl + "/ping", String.class);
    }
    
    
    public List<UsuarioDto> obtenerUsuarios() {
        String url = apiBaseUrl + "/mostrarUsuarios";  // Asegúrate de que la API tenga este endpoint
        UsuarioDto[] usuarios = restTemplate.getForObject(url, UsuarioDto[].class);
        return Arrays.asList(usuarios); // Convertir array a lista
    }
}
