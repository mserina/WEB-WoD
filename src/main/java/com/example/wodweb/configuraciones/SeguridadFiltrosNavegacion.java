package com.example.wodweb.configuraciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.wodweb.controladores.PaginaPrincipal;

/**
 * Configuración de seguridad para la aplicación, definiendo los filtros de navegación y 
 * los permisos de acceso según roles y autenticación de usuarios.
 * 
 * msm - 060325
 */
@Configuration
public class SeguridadFiltrosNavegacion {

  
    
    /** Proveedor de autenticación personalizado para validar usuarios */
    @Autowired
    private AutenticacionUsuario autenticacionUsuario = new AutenticacionUsuario();
    
    public static final Logger log = LoggerFactory.getLogger(PaginaPrincipal.class);
    
    
    /* /////////////////////////////////// */
    /*             METODOS                  */
    /* //////////////////////////////////// */

    /**
     * Configura la seguridad de la aplicación, estableciendo reglas de acceso,
     * formularios de login y logout, y la autenticación personalizada.
     * 
     * @param http Objeto HttpSecurity para definir las reglas de seguridad
     * @return SecurityFilterChain configurada con las reglas de seguridad establecidas
     * @throws Exception Si ocurre algún error en la configuración
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (para simplificar; en producción debe configurarse adecuadamente)
            .csrf(csrf -> csrf.disable())

            // 2. Configurar las reglas de acceso
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso sin autenticación a login, registro y recursos estáticos
                .requestMatchers("/login", "/registro", "/registroDatos", "/", "/css/**", "/images/**").permitAll()
                
                // Restringir el acceso a las rutas de administrador solo a usuarios con rol ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )

            // 3. Configurar el formulario de login
            .formLogin(form -> form
                .loginPage("/login")              // URL de la página de login personalizada
                .defaultSuccessUrl("/", true)     // Redirigir a "/" tras login exitoso
                .permitAll()                      // Permitir el acceso a la página de login
                .usernameParameter("email")       // Especificar que el campo de usuario es "email"
            )

            // 4. Configurar el logout
            .logout(logout -> logout
                .logoutUrl("/logout")  // URL para cerrar sesión
                .logoutSuccessUrl("/login?logout") // Redirigir a login tras cerrar sesión
                .permitAll()                      // Permitir acceso al logout
            );

        // 5. Registrar el proveedor de autenticación personalizado
        http.authenticationProvider(autenticacionUsuario);

        // 6. Construir y devolver la configuración de seguridad
        return http.build();
    }
}
