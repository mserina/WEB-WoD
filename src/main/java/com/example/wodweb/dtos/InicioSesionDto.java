package com.example.wodweb.dtos;

/**
 * Contiene los campos del login
 * msm - 060325
 */
public class InicioSesionDto {

	
	private String correoElectronico;
	private String contrasena;
	
	
	public String getCorreoElectronico() {
		return correoElectronico;
	}
	public void setCorreoElectronico(String correoElectronico) {
		this.correoElectronico = correoElectronico;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	
	
	public InicioSesionDto(String correoElectronico, String contrasena) {
		super();
		this.correoElectronico = correoElectronico;
		this.contrasena = contrasena;
	}
	
	public InicioSesionDto() {}
}