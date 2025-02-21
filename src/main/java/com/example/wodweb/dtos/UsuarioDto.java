package com.example.wodweb.dtos;

import java.time.LocalDateTime;

public class UsuarioDto {
	 private String nombreCompleto;
	    private String movil;
	    private String correoElectronico;
	    private String tipoUsuario;
	    private String contrasena;
	    private String foto;
	    private String token;
	    private LocalDateTime creacionToken;
	    private LocalDateTime expiracionToken;
	    
	    // Getters y Setters
	    public String getNombreCompleto() {
	        return nombreCompleto;
	    }

	    public void setNombreCompleto(String nombreCompleto) {
	        this.nombreCompleto = nombreCompleto;
	    }

	    public String getMovil() {
	        return movil;
	    }

	    public void setMovil(String movil) {
	        this.movil = movil;
	    }

	    public String getCorreoElectronico() {
	        return correoElectronico;
	    }

	    public void setCorreoElectronico(String correoElectronico) {
	        this.correoElectronico = correoElectronico;
	    }

	    public String getTipoUsuario() {
	        return tipoUsuario;
	    }

	    public void setTipoUsuario(String tipoUsuario) {
	        this.tipoUsuario = tipoUsuario;
	    }

	    public String getContrasena() {
	        return contrasena;
	    }

	    public void setContrasena(String contrasena) {
	        this.contrasena = contrasena;
	    }

	    public String getFoto() {
	        return foto;
	    }

	    public void setFoto(String foto) {
	        this.foto = foto;
	    }

	    public String getToken() {
	        return token;
	    }

	    public void setToken(String token) {
	        this.token = token;
	    }

	    public LocalDateTime getCreacionToken() {
	        return creacionToken;
	    }

	    public void setCreacionToken(LocalDateTime creacionToken) {
	        this.creacionToken = creacionToken;
	    }

	    public LocalDateTime getExpiracionToken() {
	        return expiracionToken;
	    }

	    public void setExpiracionToken(LocalDateTime expiracionToken) {
	        this.expiracionToken = expiracionToken;
	    }
}
