package com.api.gestion.facturas.rest;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.service.UserService;
import com.api.gestion.facturas.util.FacturaUtils;
import com.api.gestion.facturas.wrapper.UserWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    //un objeto de tipo ResponseEntity<String>, lo que indica que la respuesta tendrá un cuerpo de tipo String junto con el estado de la respuesta HTTP (como 200 OK o 400 Bad Request).
    // Map<String, String>: Un mapa que almacena pares clave-valor, ambos de tipo String. requestMap: El nombre de la variable que representa este mapa. El mapa requestMap contiene información enviada por el cliente,
   //POST: http://localhost:8081/api/v1/user/signup   JSON { "nombre":"Gema", "numeroDeContacto":"45215875", "email":"Gema@gmail.com", "password":"123456" } //el status y role no se envia, se agregará por defecto en UserServiceImpl
    @PostMapping("/signup")
    public ResponseEntity<String> registrarUsuario(@RequestBody(required = true) Map<String,String> requestMap){ //required = true indica que el cuerpo de la solicitud HTTP (request body) es obligatorio.
        try{
            return userService.signUp(requestMap); //al hacer el registro hace el return sale.
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //POST:     http://localhost:8081/api/v1/user/login  - {"email":"jas@gmail.com", "password":"123456"}   ->Para pasar de role User a Admin Ir a BD y cambiar campo status a true. Ejecutar: update users set status="true" where nombre="jas";
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody(required = true) Map<String,String> requestMap){
        try{
            return userService.login(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Obtener la lista de Usuarios con Role='user' - GET: http://localhost:8081/api/v1/user/get   -Antes hacer un login y copiar el token y pegarlo en Postman: Seleccionar: Authorization>Auth Type>Elegir: Bearer Token y pegarlo > hacer la peticion, El usuario debe ser Role Admin y tener status:'true'
    @GetMapping("/get")
    public ResponseEntity<List<UserWrapper>> listarUsuarios(){
        try{
            return userService.getAllUsers();  //retorna la lista de usuarios y HttpStatus.OK.
        }catch (Exception exception){
            exception.printStackTrace();
        }
        //devuelve una lista vacía (new ArrayList<>()) junto con el código de error HttpStatus.INTERNAL_SERVER_ERROR (500). ya que eso deve devolver el metodo: ResponseEntity<List
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //Actualizar el status(de 'false'(por defecto) a 'true') del usuario.   PUT: http://localhost:8081/api/v1/user/update  JSON: { "id":"6", "status":"true" }   NOTA Antes de hacer peticion hacer login y copiar el token y pegarlo en postman: Seleccionar: Authorization>Auth Type>Elegir: Bearer Token y pegarlo > hacer la peticion, El usuario debe ser Role Admin
    @PutMapping("/update")
    public ResponseEntity<String> actualizarUsuario(@RequestBody(required = true) Map<String,String> requestMap){
        try{
            return userService.update(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Validar token: GET: http://localhost:8081/api/v1/user/checkToken  NOTA: Antes realizar un login y copiar el token generado y pegarlo en Authorization>Auth Type>Bearer Token.
    @GetMapping("/checkToken")
    public ResponseEntity<String> validarToken(){
        try {
            return userService.checkToken();
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Cambiar contraseña a usuario actual Logeado: POST: http://localhost:8081/api/v1/user/changePassword   JSON: {"oldPassword":"123456", "newPassword":"12345"}  NOTA: Antes realizar un login y copiar el token generado y pegarlo en Authorization>Auth Type>Bearer Token.
    @PostMapping("/changePassword")
    public ResponseEntity<String> cambiarContraseña(@RequestBody Map<String,String> requestMap){
        try {
            return userService.changePassword(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Recuperar contraseña: POST: http://localhost:8081/api/v1/user/forgotPassword    JSON: { "email":"gerson21millones@gmail.com"}  No Necesita agregar token
    @PostMapping("/forgotPassword")
    public ResponseEntity<String> recuperarContraseña(@RequestBody Map<String,String> requestMap){
        try {
            return userService.forgotPassword(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
