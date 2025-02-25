package com.example.wodweb.dtos;

public class UsuarioDto {

	private Long id;

	private String nombreCompleto;

	private String movil;

	private String correoElectronico;

	private String tipoUsuario;

	private String contrasena;

	private String foto;

	// Getters y Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	@Override
	public String toString() {
		return "UsuarioModelo{" + "id=" + id + ", nombreCompleto='" + nombreCompleto + '\'' + ", movil='" + movil + '\''
				+ ", correoElectronico='" + correoElectronico + '\'' + ", tipoUsuario='" + tipoUsuario + '\'' + '}';
	}
}
