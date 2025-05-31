package com.example.wodweb.servicios;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.wodweb.dtos.UsuarioDto;
import com.example.wodweb.excepciones.CorreoExistenteExcepcion;
import com.example.wodweb.excepciones.UsuarioNoEncontradoExcepcion;

/**
 * Servicio que contiene la lógica de las funcionalidades de los usuarios.
 * msm - 050325
 */
@Service
public class UsuarioServicio {
 
    private  RestTemplate restTemplate;    
    private  String apiUrl;
    private  SecureRandom random = new SecureRandom();
    private  int CODIGO_LONGITUD = 6;
    	
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private PasswordEncoder cifradoContraseña;
    @Autowired
	private Environment env;

    public UsuarioServicio() {
        this.restTemplate = new RestTemplate();
        this.apiUrl = "http://localhost:9511/usuario";
    }

     
    /* /////////////////////////////////// */
    /*             METODOS                  */
    /* //////////////////////////////////// */

    
    
    /**
     * Obtiene la lista de todos los usuarios.
     * msm - 010325
     * @return Lista con todos los usuarios.
     */
    public List<UsuarioDto> obtenerUsuarios() {
        String url = apiUrl + "/mostrarUsuarios";
        UsuarioDto[] usuarios = restTemplate.getForObject(url, UsuarioDto[].class);
        return Arrays.asList(usuarios);
    }

    /**
     * Obtiene un usuario a traves del correo
     * msm - 010325
     * @param correo Correo del usuario
     * @return El Dto del usuario
     */
    public UsuarioDto buscarUsuario(String correo) {
    	UsuarioDto usuarioEncontrado = null;
    	List <UsuarioDto> usuarios = obtenerUsuarios();
    	for (UsuarioDto usuario : usuarios) {
    		if(usuario.getCorreoElectronico().equals(correo)) {
    			usuarioEncontrado = usuario;
    		}
    	}
    	
    	return usuarioEncontrado;
    }
    
    
    /**
     * Registra un nuevo usuario.
     * msm - 050325
     * @param usuarioCredenciales Datos del usuario a registrar.
     * @return Usuario registrado o null si ocurre un error.
     */
    public UsuarioDto registrarUsuario(UsuarioDto usuarioCredenciales, byte[] fotoBytes) {
    	
    	// 1) Primero, las validaciones habituales
        List<UsuarioDto> usuarios = obtenerUsuarios();
        boolean correoEnUso = usuarios.stream()
            .anyMatch(u -> u.getCorreoElectronico()
                            .equalsIgnoreCase(usuarioCredenciales.getCorreoElectronico()));
        if (correoEnUso) {
            throw new CorreoExistenteExcepcion("El correo ya está en uso.");
        }
        
        // 2) Cifrar la contraseña
        String contrasenaSinEncriptar = usuarioCredenciales.getContrasena();
        String contrasenaEncriptada = cifradoContraseña.encode(contrasenaSinEncriptar);
        usuarioCredenciales.setContrasena(contrasenaEncriptada);
    	
    	try {
    		// 3) Montar el cuerpo multipart
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // Campos de texto
            body.add("nombreCompleto", usuarioCredenciales.getNombreCompleto());
            body.add("movil",           usuarioCredenciales.getMovil());
            body.add("correoElectronico", usuarioCredenciales.getCorreoElectronico());
            body.add("tipoUsuario",     usuarioCredenciales.getTipoUsuario());
            body.add("contrasena",      usuarioCredenciales.getContrasena());

            // La foto como recurso
            ByteArrayResource archivoFoto = new ByteArrayResource(fotoBytes) {
                @Override public String getFilename() {
                    // Aquí podrías derivar la extensión real si quisieras
                    return "perfil.jpg";
                }
            };
    		
            body.add("foto", archivoFoto);
            
            // 4) Cabeceras multipart
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 5) Envío
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<UsuarioDto> response = restTemplate.postForEntity(apiUrl + "/crear", request, UsuarioDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error al registrar usuario en la API externa: " + e.getMessage());
            return null;
        }
    }
    

    
    /**
     * Modifica un usuario existente.
     * msm - 050325
     * @param correoElectronico Correo del usuario a modificar.
     * @param campo Campo a actualizar.
     * @param nuevoValor Nuevo valor del campo.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean editarUsuario(String correoElectronico, String campo, String nuevoValor) {
        String url = apiUrl + "/modificarUsuarios?correoElectronico=" + correoElectronico + "&campo=" + campo + "&nuevoValor=" + nuevoValor;
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, null, String.class);
        return response.getStatusCode() == HttpStatus.OK;
    }

    
    
    /**
     * Elimina un usuario por su ID.
     * msm - 050325
     * @param id ID del usuario a eliminar.
     * @param correoElectronicoUsuario Correo del usuario
     * @return true si el usuario fue eliminado con éxito, false en caso de error.
     */
    public boolean borrarUsuario(Long id, String correoElectronicoUsuario) {
    	
    	try {
            String url = apiUrl + "/borrar/" + id;
            ResponseEntity<?> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error al borrar usuario en la API externa: " + e.getMessage());
            return false;
        }
    }
    
    
    // ----------------- ALTA CORREO ---------------------- //
    
    /**
     * Genera un codigo aleatorio para el alta de usuario
     * msm - 110425
     * @return Codigo de 6 digitos
     */
    public String generarCodigo() {
        StringBuilder codigo = new StringBuilder(CODIGO_LONGITUD);
        for (int i = 0; i < CODIGO_LONGITUD; i++) {
            // Genera un dígito aleatorio (0-9)
            int digito = random.nextInt(10);
            codigo.append(digito);
        }
        return codigo.toString();
    }
    
    
    
    /**
     * Envía un correo electrónico simple.
     * 
     * @param destinatario Correo del receptor
     * @param asunto Asunto del mensaje
     * @param mensaje Cuerpo del mensaje
     */
    public void enviarCorreo(String destinatario, String asunto, String mensaje) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(destinatario);
        mailMessage.setSubject(asunto);
        mailMessage.setText(mensaje);
        mailSender.send(mailMessage);
    }

   /**
    * Verifica que un usuario a completado el registro
    * msm - 310525
    * @param correoPendiente Correo del usuario verificado
    */
	public void marcarUsuarioComoVerificado(String correoPendiente) {
		List<UsuarioDto> usuarios = obtenerUsuarios();
        for (UsuarioDto usuario : usuarios) {
            if (correoPendiente.equals(usuario.getCorreoElectronico())) {
                editarUsuario(correoPendiente, "verificado", "true");
            }
        }
	}
	
	
	
	/**
     * Llama al endpoint POST /request de la API, enviando { "email": ... }
     * y devuelve el token generado.
     *
     * @param email el correo del usuario que solicita la recuperación
     * @return el token devuelto por la API
     * @throws RuntimeException si la API responde error
     */
    public void enviarTokenDeRecuperacion(String correo) {
        try {
            // 1. Preparar la solicitud al endpoint /request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, String> body = Map.of("email", correo);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 2. Llamar a la API para obtener el token
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl + "/peticionIntrucciones", request, Map.class);

            
            if (response.getBody() != null && response.getBody().get("token") != null) {
                String token = (String) response.getBody().get("token");

                // 3. Construir el enlace de recuperación
                String perfil = env.getActiveProfiles().length > 0
	                    ? env.getActiveProfiles()[0]
	                    : "default";
                
                String link;
                if(perfil.equals("local")){
                    link = "http://localhost:8080/reiniciarContrasena?token=" + token;
                }
                else {
                    link = "http://msm-sevilla.es/reiniciarContrasena?token=" + token;
                }

                
                // 4. Enviar el correo con JavaMailSender (o como lo tengas configurado)
                String asunto = "Recuperar Contraseña";
                String mensaje = "Haz clic aquí para restablecer tu contraseña:\n" + link;

                
                enviarCorreo(correo, asunto, mensaje);
                
            } else {
                throw new RuntimeException("Error al obtener el token de recuperación");
            }
        
        }catch (HttpClientErrorException.NotFound notFound) {
           // aquí capturamos el 404 de la API y traducimos
            throw new UsuarioNoEncontradoExcepcion("No existe ningún usuario con ese correo: " + correo);
            
    	}catch (Exception e) {
            //log.error("Error al enviar el correo de recuperación", e);
            throw new RuntimeException("No fue posible enviar el correo de recuperación");
        }
    }
    
    
    
    // ----------------- ALTA CORREO ----------------------//
    
    /**
     * Valida que el token de recuperación exista y no haya expirado.
     * Lanza IllegalArgumentException si es inválido o expirado.
     * msm - 290424
     */
    public void validarToken(String token) {
        try {
            // Asumimos que la API expone GET /validarToken?token=…
            ResponseEntity<Void> resp = restTemplate.getForEntity(apiUrl + "/validarToken?token=" + token, Void.class);
            
            // Si devuelve 200 OK, token válido; si 4xx, saltará excepción
        } catch (HttpClientErrorException.NotFound ex) {
            throw new IllegalArgumentException("Token inválido");
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new IllegalArgumentException("Token expirado");
        } catch (Exception ex) {
            throw new RuntimeException("Error al validar token");
        }
    }
  
    /**
     * Manda a la API el token del usuario y la nueva contraseña que ingreso
     * msm - 020525
     * @param token
     * @param contrasenaNueva
     */
    public void reiniciarContrasena(String token, String contrasenaNueva) {
        
    	HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String,String> body = Map.of("token", token, "contrasenaNueva", contrasenaNueva);
        HttpEntity<Map<String,String>> req = new HttpEntity<>(body, headers);

        // Lanza excepción si la API responde 4xx/5xx
        restTemplate.postForEntity(apiUrl + "/reiniciarContrasena", req, Void.class);
    }
}
