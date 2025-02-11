package com.api.gestion.facturas.dao;

import com.api.gestion.facturas.pojo.Producto;
import com.api.gestion.facturas.wrapper.ProductoWrapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoDAO  extends JpaRepository<Producto,Integer> { //recibe la Clase: Producto y su tipo de dato de ID

    List<ProductoWrapper> getAllProductos();

    //Consultas usando API de JDBC por lo que se definio la consulta en la clase Producto
    @Modifying //indica que el metodo sera tipo modificador: delete, update, insert
    @Transactional
    Integer updateStatus(@Param("status") String status, @Param("id") Integer id); //se envia 2 parametros. status el primer y id el segundo.

    List<ProductoWrapper> getProductoByCategoria(@Param("id") Integer id);

    ProductoWrapper getProductoById(@Param("id") Integer id);

    //Actividad- QueryPersonalizado para buscar producto por nombre, usar RequestParam - GET
    //Actividad- Agregar nuevo campo (Fecha de creacion del producto) Listar productos paginados de forma ascendente y descendente.


}
