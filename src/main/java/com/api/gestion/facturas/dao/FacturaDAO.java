package com.api.gestion.facturas.dao;

import com.api.gestion.facturas.pojo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FacturaDAO extends JpaRepository<Factura,Integer> { //Recibe Factura y su tipo de dato de Id

        List<Factura> getFacturas();

        //Consultas usando API de JDBC por lo que se definio la consulta en la clase Factura
        List<Factura> getFacturaByUsername(@Param("username") String username);

    }

