package com.api.gestion.facturas.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    //Guia -→ https://www.geeksforgeeks.org/spring-boot-sending-email-via-smtp/

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list)  {
        SimpleMailMessage message = new SimpleMailMessage(); //crea una instancia de SimpleMailMessage llamada message
        message.setFrom("gersonescobedo.p@gmail.com"); //correo actual de quien envia el correo(admin envia)
        message.setTo(to); //a quien va dirigido el mensaje
        message.setSubject(subject); //asunto
        message.setText(text);

        if(list != null && list.size() > 0){ //si lista no es nula y lista es > 0
            //Establece los destinatarios en "Copia de Carbón" (Cc) del mensaje, es decir, aquellos que recibirán el correo pero no están en el campo principal de destinatarios (To).
            message.setCc(getCcArray(list));
        }

        javaMailSender.send(message);
    }


    //convierte la lista de correos electrónicos (list) en un arreglo (String[]), porque setCc() necesita un arreglo de cadenas.
    //Método convierte una lista de cadenas (List<String>) en un arreglo de cadenas (String[]).
    private String[] getCcArray(List<String> cclist){ // String[]: El método devuelve un arreglo de cadenas. y recibe una lista de cadena cclist
        String [] cc = new String[cclist.size()]; //Crea un arreglo de cadenas (cc) con un tamaño igual al número de elementos de la lista cclist.
        for(int i = 0;i < cclist.size();i++){ //comienza a iterar de 0, hasta < i sea menor que el tamaño de la lista y aumenta en 1
            cc[i] = cclist.get(i); //cclist.get(i): Obtiene el elemento en la posición i de la lista. y cc[i] = Guarda ese elemento en la posición i del arreglo cc,
        }
        return cc; //Devuelve el arreglo cc que contiene todos los elementos de la lista
    }

    public void forgotPassword(String to,String subject,String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        helper.setFrom("gersonescobedo.p@gmail.com"); //quien envia el correo(admin envia)
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlMessage = "<p><b>Sus detalles de inicio de sesión para el sistema de facturas</b> <br> <b>Email : </b> "
                + to + "<br><b>Password : </b> "
                + password + "</p>";
        message.setContent(htmlMessage,"text/html");
        javaMailSender.send(message);
    }

}
