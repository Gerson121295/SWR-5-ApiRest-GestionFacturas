package com.api.gestion.facturas.service.impl;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.dao.UserDAO;
import com.api.gestion.facturas.pojo.User;
import com.api.gestion.facturas.security.CustomerDetailsService;
import com.api.gestion.facturas.security.jwt.JwtFilter;
import com.api.gestion.facturas.security.jwt.JwtUtil;
import com.api.gestion.facturas.service.UserService;
import com.api.gestion.facturas.util.EmailUtils;
import com.api.gestion.facturas.util.FacturaUtils;
import com.api.gestion.facturas.wrapper.UserWrapper;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
//@Slf4j   //habilita el uso del registro de logs (logging) mediante la biblioteca SLF4J. Se crea una instancia de un logger log que puede ser utilizada para registrar mensajes en diferentes niveles (info, debug, error, etc.).
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class); //habilita el uso de log - la anotacion @Slf4j  no me funciona debido a que esta importada de lombok

    @Autowired
    private UserDAO userDAO;//Inyectar el DAO - Es como el repository que accede a la BD

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private EmailUtils emailUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Método para registrar un usuario.
     * @param requestMap Mapa que contiene pares clave-valor de datos del usuario (ejemplo: "username", "password").
     * @return ResponseEntity con un cuerpo de tipo String y el estado HTTP de la respuesta.
     */
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) { //registra al usuario mediante el mapa requestMap
        log.info("Registro interno de un usuario {}",requestMap); //imprime el map en el log

        try{
            if(validateSignUpMap(requestMap)){ //si esta valido, la validacion de los campos del usuario en validateSignUpMap es ok(true) procede a entrar al if
                User user = userDAO.findByEmail(requestMap.get("email")); //busca al usuario por email en la BD y guarda la respuesta en user

                if(Objects.isNull(user)){ //si user es nulo. Si user con el email buscado es nulo entonces, no existe en la BD permite crear el usuario con ese email
                    // Codificar la contraseña antes de guardarla en la base de datos
                    String encodedPassword = passwordEncoder.encode(requestMap.get("password"));
                    requestMap.put("password", encodedPassword);

                    //Registra al usurio en la BD
                    userDAO.save(getUserFromMap(requestMap));
                    return FacturaUtils.getResponseEntity("Usuario registrado con éxito",HttpStatus.CREATED);
                }
                else{ //si usuario ya existe en la BD responde Usuario ya existe con ese email
                    return FacturaUtils.getResponseEntity("El usuario con ese email ya existe", HttpStatus.BAD_REQUEST); //Muestra las constantes con la DATA
                }
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST); //Muestra las constantes con la DATA
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Dentro de login");
        try{
            //Autenticando el usuario
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password"))
            );

            if(authentication.isAuthenticated()){//Si usuario esta autenticado
                if(customerDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")){ //getUserDetail() obtiene el objeto del usuario autenticado y accede al valor del campo status, para validar si el status es igual a "true" el rol del user es Admin
                    return new ResponseEntity<String>( //retorna el token de acceso que permitira acceder a las peticiones
                            "{\"token\":\"" +
                                    jwtUtil.  generateToken(customerDetailsService.getUserDetail().getEmail(),
                                            customerDetailsService.getUserDetail().getRole()) + "\"}",
                            HttpStatus.OK);
                }
                else{ //si el campo status del usuario es false, el usuario tiene role user,
                    return new ResponseEntity<String>("{\"mensaje\":\""+" Espera la aprobación del administrador "+"\"}",HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception exception){
            log.error("{}",exception);
        }
        return new ResponseEntity<String>("{\"mensaje\":\""+" Credenciales incorrectas "+"\"}",HttpStatus.BAD_REQUEST);
    }


    //Retorna a los Usuarios con Role "user" por medio de un UserWrapper o UserDTO
    @Override
    public ResponseEntity<List<UserWrapper>> getAllUsers() {
        try{
            if(jwtFilter.isAdmin()){ //jwtFilter es admin //si el usuario que hace la peticion es Role 'admin',
                return new ResponseEntity<>(userDAO.getAllUsers(),HttpStatus.OK); //retorna la lista de usuarios con role "user" y el estatus OK.
            }
            else{ //si el usuario que hace la peticion no es Role: 'admin'
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private void enviarCorreoToAdmins(String status,String user,List<String> allAdmins){
        allAdmins.remove(jwtFilter.getCurrentUser()); //Borramos el administrador actual ya que el enviará el mensaje y no le llegara el mensaje a el, sino a todos los admins
        if(status != null && status.equalsIgnoreCase("true")){ //si estatus es diferente de null(no esta vacio) y status es "true"
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Cuenta aprobada","USUARIO : " +user+ "\n es aprobado por \nADMIN : " + jwtFilter.getCurrentUser(),allAdmins);
        }
        else{ //Clase emailUtils -→ metodo: sendSimpleMessage(String to, String subject, String text, List<String> list)  {
            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Cuenta desaprobada","USUARIO : " +user+ "\n es desaprobado por \nADMIN : " + jwtFilter.getCurrentUser(),allAdmins);
        }
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){ //jwtFilter es admin //si el usuario que hace la peticion es Role 'admin',
                Optional<User> optionalUser = userDAO.findById(Integer.parseInt(requestMap.get("id"))); // Busca un usuario por su ID en la BD y encapsula el resultado en un Optional. Si el usuario existe, el Optional estará presente; de lo contrario, estará vacío.
                if(!optionalUser.isEmpty()){ //si optionalUser es diferente de vacio, no esta vacio
                    userDAO.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id"))); //cambia el campo status, el metodo updateStatus recibe el status y id(en Integer)

                    //Llama a funcion que envia el correo a Admins
                    System.out.println(jwtFilter.getCurrentUser());
                    enviarCorreoToAdmins(requestMap.get("status"),optionalUser.get().getEmail(),userDAO.getAllAdmins());

                    return FacturaUtils.getResponseEntity("Status del usuario actualizado",HttpStatus.OK);
                }
                else{ //si el optionalUser esta vacio, no existe el usuario con ese id
                    FacturaUtils.getResponseEntity("Este usuario no existe",HttpStatus.NOT_FOUND);
                }
            }
            else{ //si el usuario que hace la peticion no es Role: 'admin'
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Retorna un ResponseEntity con mensaje true y status ok 200
    @Override
    public ResponseEntity<String> checkToken() {
        return FacturaUtils.getResponseEntity("true",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{
            User user = userDAO.findByEmail(jwtFilter.getCurrentUser());//obtiene el user actual.
            if(!user.equals(null)){ //valida si user es diferente de null
                //if(user.getPassword().equals(requestMap.get("oldPassword"))){ //si la contaseña del usuario es igual a oldPassword
                    //user.setPassword(requestMap.get("newPassword")); //se establece la nueva contraseña

                // Verifica si la contraseña antigua proporcionada coincide con la almacenada// Verifica si la contraseña antigua proporcionada coincide con la almacenada
                if (passwordEncoder.matches(requestMap.get("oldPassword"), user.getPassword())) {
                    // Encripta la nueva contraseña antes de guardarla
                    String encodedNewPassword = passwordEncoder.encode(requestMap.get("newPassword"));
                    user.setPassword(encodedNewPassword);

                    userDAO.save(user);
                    return FacturaUtils.getResponseEntity("Contraseña actualizada con éxito",HttpStatus.OK);
                }
                //contraseña ingresada por el user es incorrecta
                return FacturaUtils.getResponseEntity("Contraseña incorrecta",HttpStatus.BAD_REQUEST);
            }
            return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.BAD_REQUEST);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try{
            User user = userDAO.findByEmail(requestMap.get("email")); //encuentra al usuario por el correo y lo guarda en user

            //COde Prof: retorna el contraseña al correo encriptada- No funcionaria xq al ingresar el login se encriptaria y luego valida con la encriptada que tenia y es
            /* if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) { //si user no es nulo y el email ingresado no esta vacio
                emailUtils.forgotPassword(user.getEmail(), "Credenciales del sistema gestión de facturas", user.getPassword());
            }
            return FacturaUtils.getResponseEntity("Verifica tus credenciales",HttpStatus.OK);
            */

            // Generar una nueva contraseña temporal que se le envia al email del usuario para que la utilice
            String tempPassword = generateTemporaryPassword();

            // Codificar la nueva contraseña antes de guardarla en la base de datos
            user.setPassword(passwordEncoder.encode(tempPassword));
            userDAO.save(user);

            // Enviar la nueva contraseña por correo
            emailUtils.forgotPassword(
                    user.getEmail(),
                    "Credenciales del sistema gestión de facturas",
                    "Tu nueva contraseña temporal es: " + tempPassword
                    +" recomendable que inicies sesion y cambies tu contraseña de forma inmediata."
            );

            return FacturaUtils.getResponseEntity("Verifica tus credenciales", HttpStatus.OK);




            //Asistencia del Usuario para Reestablecer la Contraseña. By: GPT
 /*           if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
            String resetToken = UUID.randomUUID().toString();
            user.setResetToken(resetToken);
            userDAO.save(user);

                String resetLink = "http://localhost:8080/api/v1/user/reset-password?token=" + resetToken;

                emailUtils.forgotPassword(user.getEmail(),
                        "Restablecimiento de Contraseña",
                        "Haz clic en el siguiente enlace para restablecer tu contraseña: " + resetLink);

                return FacturaUtils.getResponseEntity("Correo de restablecimiento enviado", HttpStatus.OK);
            }
*/

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 9); // Genera una contraseña aleatoria de 8 caracteres
    }


    //Metodo para validar que el usuario a registrar tenga nombre, numeroDeContacto, email y password
    private boolean validateSignUpMap(Map<String, String> requestMap){
        //si requestMap el mapa que recibe contiene nombre y requestMap. contiene numeroDeContacto y
        if(requestMap.containsKey("nombre") && requestMap.containsKey("numeroDeContacto") && requestMap.containsKey("email") && requestMap.containsKey("password")){
            return true; //si todo ok entonces el User podra registrarse
        }
        return false; //si requestMap no contiene uno de esos campos retorna false.
    }

    //Metodo para Optener al usuario del Mapa requestMap, requestMap contiene información enviada por el cliente, para agregalo al objeto user que se retornará
    private User getUserFromMap(Map<String, String> requestMap){
        User user = new User(); //crea instancia de User llamada user
        //establece los valores a los campos del objeto user por lo tanto la informacion se extrae del mapa requestMap
        user.setNombre(requestMap.get("nombre"));
        user.setNumeroDeContacto(requestMap.get("numeroDeContacto"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false"); //el usuario a crear tendra el status false por defecto
        user.setRole("user"); //el usuario a crear tendra el rol user por defecto
        return user; //retorna al user creado
    }


}
