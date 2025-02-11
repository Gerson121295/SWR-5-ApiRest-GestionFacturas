package com.api.gestion.facturas.service.impl;


import com.api.gestion.facturas.dao.CategoriaDAO;
import com.api.gestion.facturas.dao.FacturaDAO;
import com.api.gestion.facturas.dao.ProductoDAO;
import com.api.gestion.facturas.dao.UserDAO;
import com.api.gestion.facturas.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Autowired
    private FacturaDAO facturaDAO;

    @Autowired
    private UserDAO userDAO;

    @Override
    public ResponseEntity<Map<String, Object>> getCount() {
        Map<String,Object> map = new HashMap<>();
        map.put("productos",productoDAO.count()); //metodo count() del repository permite obtener la cantidad de registros de una tabla
        map.put("categorias",categoriaDAO.count()); //cantidad de categorias que hay
        map.put("facturas",facturaDAO.count());
        map.put("users", userDAO.count());
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
