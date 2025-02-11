package com.api.gestion.facturas.service;


import com.api.gestion.facturas.wrapper.UserWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {

    //Firma de los metodos
    /*
    ResponseEntity<String>: El método devuelve un objeto de tipo ResponseEntity<String>. Esto indica que la respuesta del método tendrá un cuerpo que es una cadena de texto (String) y también contendrá detalles sobre el estado HTTP
    Map<String, String>: Un mapa que almacena pares clave-valor, ambos de tipo String. requestMap: El nombre de la variable que representa este mapa. El mapa requestMap puede contener información enviada por el cliente,
     */
    ResponseEntity<String> signUp(Map<String,String> requestMap);

    ResponseEntity<String> login(Map<String,String> requestMap);

    ResponseEntity<List<UserWrapper>> getAllUsers();

    ResponseEntity<String> update(Map<String,String> requestMap);

    ResponseEntity<String> checkToken();

    ResponseEntity<String> changePassword(Map<String,String> requestMap);

    ResponseEntity<String> forgotPassword(Map<String,String> requestMap);

}
