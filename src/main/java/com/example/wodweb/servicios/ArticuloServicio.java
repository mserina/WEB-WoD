package com.example.wodweb.servicios;

import java.util.Arrays;
import java.util.List;

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
}
