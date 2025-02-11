package com.api.gestion.facturas.dao;


import com.api.gestion.facturas.pojo.User;
import com.api.gestion.facturas.wrapper.UserWrapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

//DAO o puede ser Repository

@Repository
public interface UserDAO extends JpaRepository<User, Integer> { //recibe la clase User y su tipo de dato del ID

    //Asistencia del Usuario para Reestablecer la Contraseña. By: GPT
    //Optional<User> findByResetToken(String resetToken);


    //para realizar la consulta findByEmail se utiliza el API de JDBC por lo que se definio code en la clase User
    User findByEmail(@Param(("email")) String email);

    List<UserWrapper> getAllUsers();

    List<String> getAllAdmins();

    @Transactional
    @Modifying //indica que este metodo hará operaciones de update, delete y insert(cambiara algo) no de tipo get.
    Integer updateStatus(@Param("status") String status,@Param("id") Integer id); //status es el 1er. parametro a enviar y id es el 2do. parametro.
}
