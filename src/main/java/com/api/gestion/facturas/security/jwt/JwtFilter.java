package com.api.gestion.facturas.security.jwt;

import com.api.gestion.facturas.security.CustomerDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Esta clase valida el token-Validar le usuario completo

@Component
public class JwtFilter extends OncePerRequestFilter { // OncePerRequestFilter  solo se ejecuta 1 vez

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerDetailsService customerDetailsService;

    Claims claims = null;
    private String username = null;

    // doFilterInternal procesa cada solicitud HTTP antes de que llegue a los controladores
    // doFilterInternal recibe:HttpServletRequest request: Representa la solicitud HTTP que llega al servidor. Contiene la información del cliente, encabezados, parámetros, etc
    //HttpServletResponse response: Representa la respuesta que se enviará al cliente. Puedes modificar esta respuesta (como añadir encabezados).
    //FilterChain filterChain: Permite que la solicitud continúe su flujo hacia el siguiente filtro o componente en la cadena.
    //throws ServletException, IOException: el metodo puede lanzar excepciones relacionadas con el manejo de la solicitud o entrada/salida.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //if(request.getServletPath().matches("/user/login|user/forgotPassword|/user/signup")){
        if(request.getServletPath().matches(
                "/api/v1/user/login|/api/v1/user/forgotPassword|/api/v1/user/signup" +
                        "|/api/v1/getByCategoria/.+|/api/v1/producto/getById/.+"
            )){ //Comprueba si la ruta del endpoint coincide con alguna de las tres rutas especificadas, son rutas públicas (no requieren autenticación).
            filterChain.doFilter(request,response); //Permite que la solicitud continúe su flujo hacia el siguiente filtro sin hacer validación JWT.
        }
        //Si la ruta no coincide con las rutas permitidas, entonces:
        else{
            String authorizationHeader = request.getHeader("Authorization");//Obtiene el encabezado Authorization de la solicitud (donde normalmente se incluye el token JWT).
            String token = null; //almacenará el token JWT, si está presente.

            //si authorizationHeader es diferente de null no esta vacio(tiene algo) y authorizationHeader empieza con "Bearer " - Tiene el token"Estructura del token empieza con Bearer
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring(7); //al token le quita 7 caracateres que son:"Bearer " para dejar solo el token
                //extrae data del token
                username = jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
            }
            //si username no es null, existe el usuario y ...getAuthentication() == null el usuario no esta autenticado
            if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = customerDetailsService.loadUserByUsername(username);//carga al usuario para autenticar
                if(jwtUtil.validateToken(token,userDetails)){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    new WebAuthenticationDetailsSource().buildDetails(request);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
            filterChain.doFilter(request,response); //Permite que la solicitud continúe su flujo hacia el siguiente filtro
        }
    }

    //Validacion de roles del usuario
    public Boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    public Boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    //Obtener el usuario actual
    public String getCurrentUser(){
        return username;
    }

}
