package com.example.wodweb.configuraciones;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase para codificacion de contraseña
 * msm - 010525
 */
@Configuration
public class CodificacionContraseña {

    /**
     * Bean que Spring usará para cifrar/descifrar contraseñas.
     * BCrypt es el algoritmo.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
