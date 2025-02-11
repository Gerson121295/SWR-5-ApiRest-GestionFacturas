package com.api.gestion.facturas.service;

import com.api.gestion.facturas.wrapper.ProductoWrapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public interface ProductoService {

    ResponseEntity<String> addNuevoProducto(Map<String,String> requestMap);

    ResponseEntity<List<ProductoWrapper>> getAllProductos();

    ResponseEntity<String> updateProducto(@RequestBody Map<String,String> requestMap);

    ResponseEntity<String> deleteProducto(Integer id);

    ResponseEntity<String> updateStatus(Map<String,String> requestMap);

    ResponseEntity<List<ProductoWrapper>> getByCategoria(Integer id);

    ResponseEntity<ProductoWrapper> getProductoById(Integer id);
}
