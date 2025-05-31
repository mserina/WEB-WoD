/* /////////////////////////////////// */
    /*             LISTO                  */
    /* //////////////////////////////////// */

package com.example.wodweb.servicios;

import java.net.URI;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.example.wodweb.dtos.ArticuloDto;
import com.example.wodweb.dtos.CarritoDto;
import com.example.wodweb.dtos.UsuarioDto;

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
		
		 //Definimos la url de la API
        String url = apiUrl + "/carrito/crearArticulos";
        
		//Creamos la entidad carrito, con los campos del parametro
		CarritoDto peticionCarrito = new CarritoDto();
		
		peticionCarrito.setUsuarioId(usuarioId);
        peticionCarrito.setArticuloId(articuloId);
        peticionCarrito.setCantidad(cantidad);      
        
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
	  * @return Devuelve el carrito del usuario 
	  */
    public List<Map<String, ?>> obtenerCarritoDeUsuario(Long usuarioId) {
        
    	//Definimos la url de la API
    	String url = apiUrl + "/carrito/" + usuarioId;
    	
        try {
        	//Realizamos la peticion para sacar el carrito del usuario
        	ResponseEntity<List<CarritoDto>> resp = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        	
        	//Obtén los items del carrito
            List<CarritoDto> carrito = resp.getBody();

         //Recorre uno a uno y maneja excepciones individuales
            List<Map<String, ?>> carritoArticulos = new ArrayList<>();
            
            for (CarritoDto elementoCarrito : carrito) {
                try {
                    ArticuloDto articulo = articuloServicio.obtenerArticuloPorId(elementoCarrito.getArticuloId());
                    carritoArticulos.add(Map.of(
                    	"id",           elementoCarrito.getId(),
                    	"articuloId",   articulo.getId(),
                        "nombre",       articulo.getNombre(),
                        "precio",       articulo.getPrecio(),
                        "stock",        elementoCarrito.getCantidad(),
                        "subtotal",     articulo.getPrecio() * elementoCarrito.getCantidad()
                    ));
                } catch (NoSuchElementException e) {
                	
                    // Si falla para este artículo, lo registramos y seguimos
                	String mensajeError = "No se encontró el artículo con ID: " + elementoCarrito.getArticuloId() +" : "+ e.getMessage();
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
    
    /**
     * Actualiza la cantidad de un articulo
     * msm - 310525
     * @param elementoCarritoId Id del articulo en el carrito
     * @param articuloId Id del articulo
     * @param cantidad Cantidad del articulo
     * @return Devuelve la lista del carrito
     */
    public CarritoDto actualizarCantidad(Long elementoCarritoId, Long articuloId, Integer cantidad) {
		
    	// Construimos la URL con parámetros quer 
    	String url = apiUrl + "/carrito" + "/actualizar";
    	
    	//URI (Uniform Resource Identifier), sirve para añadir a la URL los parametros
    	URI uri = UriComponentsBuilder
    	        .fromUriString(url)
    	        .queryParam("articuloCarritoId", elementoCarritoId)
    	        .queryParam("articuloId", articuloId)
    	        .queryParam("cantidad", cantidad)
    	        .build()
    	        .toUri();

    	
		try {
			//Realizamos la peticion para actualizar
			ResponseEntity<CarritoDto> resp = restTemplate.postForEntity(uri ,null, CarritoDto.class);
			return resp.getBody();
			
		} catch (HttpClientErrorException e) {
			
		//En caso de error, guardamos el estado del HTTP, y lanzamos la excepcion  
		 HttpStatusCode status = e.getStatusCode();
			
		if (status == HttpStatus.BAD_REQUEST) {
			throw new IllegalArgumentException(e.getResponseBodyAsString());
			
		} else if (status == HttpStatus.NOT_FOUND) {
			throw new NoSuchElementException("Carrito o artículo no encontrado");
			
		} else {
			throw new RuntimeException("Error en la petición: " + status);
		
		}
		
		} catch (HttpServerErrorException e) {
		throw new RuntimeException("Error interno del servidor: " + e.getStatusText());
		
		} catch (Exception e) {
		throw new RuntimeException("No se pudo conectar con la API: " + e.getMessage(), e);
		
		}
    }
    
    /**
     * Borra elementos del carrito 
     * msm - 290525
     * @param elementoCarritoId Id del elemento a borrar
     * @return Devuelve la respuesta de la peticion
     */
    public boolean borrarElementoCarrito(Long elementoCarritoId) {
        try {
        	 String url = apiUrl + "/carrito/eliminarElementoCarrito/" + elementoCarritoId;            
        	 ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error al borrar elemento del carrito en la API externa: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Vacia el carrito de un usuario
     * msm - 290525
     * @param usuarioId Id del usuario al que se le va a vaciar el carrito
     * @return Devuelve la respuesta de la peticion
     */
    public boolean vaciarCarrito(Long usuarioId) {
        try {
        	 String url = apiUrl + "/carrito//vaciarCarrito/" + usuarioId;            
        	 ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
            
        } catch (HttpClientErrorException.NotFound e) {
            // 404 → no existía carrito
            return false;
            
        } catch (Exception e) {
            System.err.println("Error al vaciar el carrito en la API externa: " + e.getMessage());
            return false;
        }
    }
	
}