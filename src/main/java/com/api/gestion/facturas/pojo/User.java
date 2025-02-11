package com.api.gestion.facturas.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

//Query JDBC
@NamedQuery(name="User.findByEmail", query = "select u from User u where u.email=:email") //para realizar consulta findByEmail definida en UserDAO

//Selecciona los datos id, nombre, email, numero y status de UserWrapper de User u donde role de user u.role es = a 'user'
@NamedQuery(name = "User.getAllUsers",query = "select new com.api.gestion.facturas.wrapper.UserWrapper(u.id,u.nombre,u.email,u.numeroDeContacto,u.status) from User u where u.role='user'") //solo obtiene User con role 'user'
@NamedQuery(name = "User.updateStatus",query = "update User u set u.status=:status where u.id=:id") //Actualizar el status del usuario de 'false'(por defecto) a 'true'. NOTA: :status es el 1er. parametro que se le envia de UserDao y el :id es el 2do.  el signo: ":" significa parametro
@NamedQuery(name = "User.getAllAdmins",query = "select u.email from User u where u.role='admin'")


//@Data
@Entity

//DynamicUpdate y DynamicInsert Indica que de una tabla solo se va a insertar o actualizar una cierta cantidad de columnas no todoas, en este caso role y status no se agrega al enviar el objeto
@DynamicUpdate
@DynamicInsert
@Table(name = "users")  //definicion del nombre de la tabla en la BD users de la entidad User
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "numeroDeContacto")
    private String numeroDeContacto;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private String status;

    @Column(name = "role")
    private String role;


    //Asistencia del Usuario para Reestablecer la Contraseña - By: GPT
    // Nuevo campo para almacenar el token de restablecimiento
   /* private String resetToken;

    // Getters y Setters
    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
    */


    //Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroDeContacto() {
        return numeroDeContacto;
    }

    public void setNumeroDeContacto(String numeroDeContacto) {
        this.numeroDeContacto = numeroDeContacto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
