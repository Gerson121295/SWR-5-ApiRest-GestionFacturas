package com.api.gestion.facturas.dao;

import com.api.gestion.facturas.pojo.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoriaDAO extends JpaRepository<Categoria,Integer> {

    //en lugar de crear metodo getAllCategorias con JDBC definido en clase Categoria otra opcion es el usar el metodo findAll de JPA Repository
    List<Categoria> getAllCategorias();
}
