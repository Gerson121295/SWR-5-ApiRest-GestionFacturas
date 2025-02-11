package com.api.gestion.facturas.security.jwt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


//Clase tendra los metodos para generar, validar el token

@Service
public class JwtUtil {

    private Key secret;

    /*
    Este método es anotado con @PostConstruct, lo que significa que se ejecutará después de que la
    instancia de la clase haya sido construida y todas las dependencias hayan sido inyectadas.
    En este método, se inicializa la clave secreta que se utilizará para firmar y verificar los
    tokens JWT. Se genera una clave aleatoria utilizando SecureRandom() y se asigna a la variable secret.
    */
    @PostConstruct
    protected void init(){
        byte[] apiKeySecretBytes = new byte[64]; //Declara un arreglo de bytes de tamaño 64, equivalente a 512 bits
        new SecureRandom().nextBytes(apiKeySecretBytes); //SecureRandom genera números aleatorios seguros para criptografía. nextBytes(apiKeySecretBytes) llena el arreglo con bytes aleatorios.
        secret = Keys.hmacShaKeyFor(apiKeySecretBytes); //Convierte los bytes aleatorios generados en una clave secreta válida para la firma HMAC usando Keys.hmacShaKeyFor() (parte de la librería io.jsonwebtoken).
    }

    //El metodo extractUsername() devuelve el valor del campo sub del token JWT. Generalmente es el nombre de usuario.
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject); //Claims::getSubject: Claims es una interfaz de la librería io.jsonwebtoken. y getSubject() es el metodo que devuelve el usuario
    }

    //Metodo devuelve la fecha de expiracion del token
    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    //Metodo para extraer los Claims, token tiene los datos
    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver){ // <T> tipo generico
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /* Este método verifica y extrae todos los reclamos del token JWT. Utiliza la clave secreta para
    verificar la firma del token y luego extrae los reclamos del payload.*/
    public Claims extractAllClaims(String token){
        //return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getEncoded()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //Verifica si el token es valido
    private Boolean isTokenExpired(String token){
        //before(Date) es un metodo de la clase Date en Java. Devuelve true si la fecha de expiración (extractExpiration(token)) es anterior a la fecha y hora actual (new Date()), lo que significa que el token ya ha expirado.
        return extractExpiration(token).before(new Date()); //valida la fecha y hora de expiracion del token con la fecha y hora actual si retorna true entonces el token ya expiro
    }

    //Genera el token de acceso: recibe el username y el role
    public String generateToken(String username,String role){
        Map<String,Object> claims = new HashMap<>();
        claims.put("role",role); // Añade el rol al mapa de claims del token
        return createToken(claims,username);  // Llama a createToken para generar el token con los claims y el nombre de usuario
    }

    //Crea el token configurando las propiedades estándar (como la fecha de emisión, expiración y firma).
    private String createToken(Map<String,Object> claims,String subject){
        return Jwts.builder() // Inicia la construcción del token
                .claims(claims)  // Establece los claims personalizados (como el rol)
                .subject(subject) // Define el "subject" del token, nombre de usuario
                .issuedAt(new Date(System.currentTimeMillis())) // Establece la fecha de emisión
                .expiration(new Date(System.currentTimeMillis()  + 3600000)) //1hora  // + 60 * 60 * 10 * 1000)) // 10 horas dura  //+100*60*60*10)) // Fija la fecha de expiración
                .signWith(Keys.hmacShaKeyFor(secret.getEncoded())) // Firma el token con una clave secreta
                .compact(); // Construye y devuelve el token JWT en formato compacto
    }

    //Valida el token
    public Boolean validateToken(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
