package com.api.gestion.facturas.security;

import com.api.gestion.facturas.dao.UserDAO;
import com.api.gestion.facturas.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
//@Slf4j   //habilita el uso del registro de logs (logging) mediante la biblioteca SLF4J. Se crea una instancia de un logger log que puede ser utilizada para registrar mensajes en diferentes niveles (info, debug, error, etc.).
public class CustomerDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomerDetailsService.class); //habilita el uso de log - la anotacion @Slf4j  no me funciona debido a que esta importada de lombok

    @Autowired
    private UserDAO userDAO;

    private User userDetail;

    //Cargar Usuario por username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Dentro de loadUserByUsername {}",username);
        userDetail = userDAO.findByEmail(username); //busca al usuario por email

        if(!Objects.isNull(userDetail)){ //el objeto userDetail es diferente de null, el usuario existe
            //representar al usuario autenticado. permite que Spring Security gestione el usuario durante la autenticación y autorización. recibe: email, password y un array lista de roles (autoridades) del usuario, vacía en este caso.
            //Convierte el objeto userDetail en una instancia compatible con Spring Security, con nombre de usuario, contraseña y roles (lista vacia). Esto permite a Spring Security manejar el inicio de sesión y la seguridad del usuario.
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
        }
        else{
            throw new UsernameNotFoundException("Usuario no encontrado");
        }
    }

    // Devuelve el objeto userDetail cargado previamente por loadUserByUsername.  Permite obtener la información del usuario actual fuera del contexto de autenticación para mostrar en la interfaz o validaciones
    public User getUserDetail(){
        return userDetail;
    }


}
