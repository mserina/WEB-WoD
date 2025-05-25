package com.example.wodweb.servicios;

import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.CarritoDto;

/**
 * Servicio que contiene la lógica de las funcionalidades del carrito.
 * msm - 250525
 */
public class CarritoServicio {

	private  RestTemplate restTemplate;    
    private  String apiUrl;

    public CarritoServicio() {
        this.restTemplate = new RestTemplate();
        this.apiUrl = "http://localhost:9511/articulo";
    }
	 
    /**
     * Metodo que agrega articulos al carrito
     * msm - 250525
     * @param usuarioId id del usuario que quiere agregar el articulo
     * @param articuloId articulo que va a agregar
     * @param cantidad cantidad del articulo que se va a agregar
     * @return Devuelve el carrito con el articulo agregado
     */
	public CarritoDto agregarAlCarrito(Long usuarioId, Long articuloId, Integer cantidad) {
		CarritoDto peticionCarrito = new CarritoDto();
		
		peticionCarrito.setUsuarioId(usuarioId);
        peticionCarrito.setArticuloId(articuloId);
        peticionCarrito.setCantidad(cantidad);
        
        String url = apiUrl + "/carrito/crearArticulos";
        return restTemplate.postForObject(url, peticionCarrito, CarritoDto.class);
    }
}
