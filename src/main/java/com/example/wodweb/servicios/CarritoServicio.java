package com.example.wodweb.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.ArticuloDto;
import com.example.wodweb.dtos.CarritoDto;

/**
 * Servicio que contiene la lógica de las funcionalidades del carrito.
 * msm - 250525
 */
@Service
public class CarritoServicio {

	private  RestTemplate restTemplate;    
    private  String apiUrl;
    @Autowired
	  private ArticuloServicio articuloServicio;
    
    public CarritoServicio() {
        this.restTemplate = new RestTemplate();
        this.apiUrl = "http://localhost:9511";
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
		
		//Creamos la entidad carrito, con los campos del parametro
		CarritoDto peticionCarrito = new CarritoDto();
		
		peticionCarrito.setUsuarioId(usuarioId);
        peticionCarrito.setArticuloId(articuloId);
        peticionCarrito.setCantidad(cantidad);
        
        //Definimos la url de la API
        String url = apiUrl + "/carrito/crearArticulos";
        
        
        try {
        	//Hacemos la llamada a la API para guardar el articulo
        	ResponseEntity<CarritoDto> response = restTemplate.postForEntity(url, peticionCarrito, CarritoDto.class);
        	return response.getBody();   
        	
        }catch (HttpClientErrorException e){
        	
        	//En caso de error, guardamos el estado del HTTP, y lanzamos la excepcion  
        	HttpStatusCode status = e.getStatusCode();

             if (status == HttpStatus.CONFLICT) {
            	 
                 // 409
                 throw new IllegalStateException("Este artículo ya está en tu carrito.");
             } else if (status == HttpStatus.NOT_FOUND) {
            	 
                 // 404
                 throw new NoSuchElementException("Usuario o artículo no encontrado.");
             } else if (status == HttpStatus.BAD_REQUEST) {
            	 
                 // 400
                 throw new IllegalArgumentException("Datos inválidos: " + e.getResponseBodyAsString());
             } else {
            	 
                 // otros 4xx
                 throw new RuntimeException("Error en la petición: " + status);
             }

        } catch (HttpServerErrorException e) {
            // 5xx: errores del servidor
            throw new RuntimeException("Error interno del servidor: " + e.getStatusText());

        } catch (Exception e) {
            // otras excepciones (network, serialización, etc.)
            throw new RuntimeException("No se pudo conectar con la API: " + e.getMessage(), e);
        }
        
	}
	
	
	 /**
	  * Muestra el carrito de un usuario
	  * msm - 260525
	  * @param usuarioId id del usuario que queremos ver el carrito
	  * @return el carrito 
	  */
    public List<Map<String, ?>> obtenerCarritoDeUsuario(Long usuarioId) {
        
    	String url = apiUrl + "/carrito/" + usuarioId;
    	
        try {
        	//Realizamos la peticion para sacar el carrito del usuario
        	ResponseEntity<List<CarritoDto>> resp = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        	
        	//Obtén los items del carrito
            List<CarritoDto> elementosCarrito = resp.getBody();

         //Recorre uno a uno y maneja excepciones individuales
            List<Map<String, ?>> carritoArticulos = new ArrayList<>();
            for (CarritoDto item : elementosCarrito) {
                try {
                    ArticuloDto articulo = articuloServicio.obtenerArticuloPorId(item.getArticuloId());
                    carritoArticulos.add(Map.of(
                    	"id",       item.getId(),
                        "nombre",   articulo.getNombre(),
                        "precio",   articulo.getPrecio(),
                        "cantidad", item.getCantidad(),
                        "subtotal", articulo.getPrecio() * item.getCantidad()
                    ));
                } catch (NoSuchElementException e) {
                    // Si falla para este artículo, lo registramos y seguimos
                	String mensajeError = "No se encontró el artículo con ID: " + item.getArticuloId() +" : "+ e.getMessage();
                    throw new NoSuchElementException(mensajeError);
                }
            }
        	
            return carritoArticulos;
        } catch (HttpClientErrorException e) {
        	
        	//En caso de error, guardamos el estado del HTTP, y lanzamos la excepcion  
        	HttpStatusCode status = e.getStatusCode();
        	if (status == HttpStatus.NOT_FOUND) {
            	
        		// 404
                throw new NoSuchElementException("Carrito no encontrado para el usuario");
            } else {
            	
            	// otros 4xx
                throw new RuntimeException("Error en la petición: " + status);
            }

        } catch (HttpServerErrorException e) {
        	
            // 5xx: errores del servidor
            throw new RuntimeException("Error interno del servidor: " + e.getStatusText());

        } catch (Exception e) {
        	
            // otras excepciones (network, serialización, etc.)
            throw new RuntimeException("No se pudo conectar con la API: " + e.getMessage(), e);
        }
    }
	
}