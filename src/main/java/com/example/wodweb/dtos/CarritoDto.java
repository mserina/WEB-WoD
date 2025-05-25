package com.example.wodweb.dtos;

public class CarritoDto {
	
	private Long usuarioId;
    private Long articuloId;
    private Integer cantidad = 1;

    //Getters y Setters
    public Long getUsuarioId() {
		return usuarioId;
	}
	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}
	public Long getArticuloId() {
		return articuloId;
	}
	public void setArticuloId(Long articuloId) {
		this.articuloId = articuloId;
	}
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
}
