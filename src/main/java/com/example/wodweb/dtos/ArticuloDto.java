package com.example.wodweb.dtos;

/**
 * Campos de los articulos
 * msm - 130525
 */
public class ArticuloDto {
	
    private Long id;

	private String nombre;

    private String descripcion;

    private Integer precio;

    private Integer stock;

    private byte[] fotoArticulo;
    
    private String tipoArticulo;
    
    //Getters y Setters
    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getPrecio() {
		return precio;
	}

	public void setPrecio(Integer precio) {
		this.precio = precio;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public byte[] getFotoArticulo() {
		return fotoArticulo;
	}

	public void setFotoArticulo(byte[] fotoArticulo) {
		this.fotoArticulo = fotoArticulo;
	}

	public String getTipoArticulo() {
		return tipoArticulo;
	}

	public void setTipoArticulo(String tipoArticulo) {
		this.tipoArticulo = tipoArticulo;
	}
}
