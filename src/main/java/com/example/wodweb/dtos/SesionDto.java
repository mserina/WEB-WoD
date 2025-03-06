package com.example.wodweb.dtos;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SesionDto implements UserDetails {

    private String correo; // Correo electrónico (o nombre de usuario)
    private String contrasena;
    private String nombre; // Nombre completo del usuario
    private boolean admin;
    private Collection<? extends GrantedAuthority> authorities;

    public SesionDto(String correo, String password, String nombre, Collection<? extends GrantedAuthority> authorities) {
        this.correo = correo;
        this.contrasena = password;
        this.nombre = nombre;
        this.authorities = authorities;
    }

    // Método para obtener el nombre adicional
    public String getNombre() {
        return nombre;
    }

    // Métodos obligatorios de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    @Override
    public String getPassword() {
        return contrasena;
    }
    @Override
    public String getUsername() { //El nombre del get no se puede cambiar, de lo contrario Spring Security no podra interpretarlo
        return correo;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    public boolean isAdmin() {  // Debe ser "isAdmin" y no "getAdmin" para booleanos
        return admin;
    }
    
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
