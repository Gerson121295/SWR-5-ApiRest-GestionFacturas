package com.api.gestion.facturas.security;

import com.api.gestion.facturas.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    //Encriptar contraseña
    @Bean
    public PasswordEncoder passwordEncoder(){
        //return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    // authenticationManager define quienes pueden acceder al recurso
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager(); //authenticationConfiguration obtiene el AuthenticationManager de spring security
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable) //ya viene configurada para esta vulnerabilidad csrf
                .authorizeHttpRequests(request -> {
                    request.requestMatchers( //rutas de son de acceso publico
                            "api/v1/user/login","api/v1/user/signup","api/v1/user/forgotPassword",
                                    "api/v1/producto/getByCategoria/**","api/v1/producto/getById/**")
                            .permitAll()
                            .anyRequest()
                            .authenticated(); //cualquier otra ruta se necesita autenticacion. No tienen acceso.
                })
                //.cors(cors -> cors.configurationSource(corsConfigurationSource())) //funcion para agregar los cors para conexion con el frontend definir funcion corsConfigurationSource()

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //por defecto el SessionCreationPolicy es el Http donde se guarda la sesion del usuario es conectado.
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); //filtro validacion de token
        return httpSecurity.build();
    }

 /*
    //ejemplo de codigo para configuracion de los cors -conexion con el frontend
    //Metodos para configurar el CORS - Intercambio de origen cruzado para conexion entre el backend y el frontend,
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration  config = new CorsConfiguration();

        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET","POST","DELETE","PUT"));
        config.setAllowedHeaders(Arrays.asList("Authorization","Content-Type"));
        config.setAllowCredentials(true);

        //Instancia de url de la implementacion concreta
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); //Esta configuracion se aplica a todas las rutas de la app.
        return source;
    }

    //Filtro de Spring se ejecute en todas las rutas
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter (){
        @SuppressWarnings("null")
        FilterRegistrationBean<CorsFilter> corsBean = new FilterRegistrationBean<>(
                new CorsFilter(corsConfigurationSource()));
        //Prioridad
        corsBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return corsBean;
    }
*/
}
