package com.example.wodweb.dtos;


public class SesionDto {
	private Long idUsuario;
	private String rol;
	//private String token = "000";
	
	// Getters y Setters
	public Long getIdUsuario() {
	    return idUsuario;
	}
	
	public void setIdUsuario(Long idUsuario) {
	    this.idUsuario = idUsuario;
	}
	
	public String getRol() {
	    return rol;
	}
	
	public void setRol(String rol) {
	    this.rol = rol;
	}
	
	/*
	 * public String getToken() { return token; }
	 * 
	 * public void setToken(String token) { this.token = token; }
	 */
	
	// Constructor vacío
		public SesionDto() {}
		
	// Constructor con parámetros
		public SesionDto(Long idUsuario, String rol) {
		    this.idUsuario = idUsuario;
		    this.rol = rol;
		}
	    
}
