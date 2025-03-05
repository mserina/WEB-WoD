package com.example.wodweb.configuraciones;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadFiltrosNavegacion {

    // Inyectamos nuestro proveedor de autenticación personalizado
    @Autowired
    private AutenticacionUsuario autenticacionUsuario = new AutenticacionUsuario();

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Desactivar CSRF (para simplificar; en producción configura CSRF adecuadamente)
            .csrf(csrf -> csrf.disable())

            // 2. Configurar las reglas de acceso:
            .authorizeHttpRequests(auth -> auth
                
                .requestMatchers("/login", "/registro", "/registroDatos", "/", "/css/**", "/images/**").permitAll() // Permitir el acceso a la página de login, registro y rutas públicas sin autenticación
                .requestMatchers("/admin/**").hasRole("ADMIN")   // Las URLs bajo /admin/ requieren que el usuario tenga el rol ADMIN  
                .anyRequest().authenticated()   // Todas las demás peticiones requieren autenticación
            )

            // 3. Configurar el formulario de login:
            .formLogin(form -> form
                .loginPage("/login")              // URL de la página de login personalizada (GET /login)
                .defaultSuccessUrl("/", true)     // Tras login exitoso, redirigir a "/" (la bandera true fuerza la redirección)
                .permitAll()                      // Permite que todos puedan acceder al formulario de login
                .usernameParameter("email")  // Indica que el campo de usuario se llama "email"

            )

            // 4. Configurar el logout:
            .logout(logout -> logout
                .logoutUrl("/logout")             // URL para realizar el logout
                .logoutSuccessUrl("/login?logout")  // Tras cerrar sesión, redirigir a /login?logout
                .permitAll()                      // Permitir que todos puedan acceder al logout
            );

        // 5. Registrar nuestro AuthenticationProvider personalizado para que Spring lo use en el proceso de autenticación
        http.authenticationProvider(autenticacionUsuario);

        // 6. Construir y devolver la SecurityFilterChain
        return http.build();
    }

}
