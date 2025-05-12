package com.example.wodweb.configuraciones;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.example.wodweb.dtos.SesionDto;

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
	private static final Logger log = LoggerFactory.getLogger(AutenticacionUsuario.class);

    
    
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
                .requestMatchers("/usuario/foto/**", "/perfil" ,"/reiniciarContrasena" ,"/contrasenaOlvidada","/reenviarCodigo" ,"/verificarCodigo","/login", "/registro", "/registroDatos", "/", "/css/**", "/images/**", "/favicon.ico").permitAll()
                
                // Restringir el acceso a las rutas de administrador solo a usuarios con rol ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
                
                
            )

            // 3. Configurar el formulario de login
            .formLogin(form -> form
                .loginPage("/login")              		// URL de la página de login personalizada
                .failureHandler(loginErrorHandler())	// Llama a un handler para capturar excepciones
                .defaultSuccessUrl("/", true)     		// Redirigir a "/" tras login exitoso
                .permitAll()                      		// Permitir el acceso a la página de login
                .usernameParameter("email")       		// Especificar que el campo de usuario es "email"
            )
            
            // 4. Configurar el logout
            .logout(logout -> logout
                .logoutUrl("/logout")                               // URL para cerrar sesión
                .logoutSuccessHandler(logoutHandler())              // Redirigir a login tras cerrar sesión
                .permitAll()                                        // Permitir acceso al logout
            );

        // 5. Registrar el proveedor de autenticación personalizado
        http.authenticationProvider(autenticacionUsuario);

        // 6. Construir y devolver la configuración de seguridad
        return http.build();
    }
    
    /**
     * Handler que captura las exepciones del login
     * msm - 220425
     * @return Devuelve una instancia de AuthenticationFailureHandler
     */
    @Bean
    public AuthenticationFailureHandler loginErrorHandler() {
        return (request, response, exception) -> {
            String msg;
            if (exception instanceof DisabledException) {
                // Si la cuenta está deshabilitada, mostramos el mensaje que venga en la excepción
                msg = exception.getMessage();
                
            } else if (exception instanceof BadCredentialsException) {
                // Si la contraseña o el usuario son incorrectos
                msg = "Usuario o contraseña incorrectos";
                
            } else {
                // Para cualquier otro error (conexión, etc.)
                msg = "Error interno, inténtalo más tarde";
                
            }
            // Codificamos el mensaje para pasarlo por URL (para evitar caracteres inválidos)
            String encoded = URLEncoder.encode(msg, StandardCharsets.UTF_8);
            // Redirigimos al login añadiendo ?error=<mensaje codificado>
            response.sendRedirect("/login?error=" + encoded);
        };
    }
    
    
    /**
     * LogoutSuccessHandler personalizado para realizar logueo de la acción de logout
     * justo antes de invalidar la sesión del usuario.
     * msm - 220425
     * @return Devuelve una instancia de LogoutSuccessHandler
     */
    @Bean
    public LogoutSuccessHandler logoutHandler() {
        return (request, response, authentication) -> {
            // Antes de invalidar la sesión, verificamos si el principal es de tipo SesionDto
            if (authentication != null && authentication.getPrincipal() instanceof SesionDto) {
                SesionDto sesion = (SesionDto) authentication.getPrincipal();
                // Logueamos el cierre de sesión con el nombre del usuario
                log.info(sesion.getNombre() + " ha cerrado la sesión");
            } else {
                // En caso de que no se encuentre un usuario autenticado, logueamos un mensaje genérico
            	log.info("Se ha realizado un logout (usuario no autenticado)");
            }
            // Redireccionamos a la página de login con un parámetro que indique logout
            response.sendRedirect("/login?logout");
        };
    }
}
