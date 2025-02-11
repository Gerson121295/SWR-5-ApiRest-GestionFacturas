package com.api.gestion.facturas.service;

import com.api.gestion.facturas.pojo.Categoria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CategoriaService {

    ResponseEntity<String> addNuevaCategoria(Map<String,String> requestMap);

    ResponseEntity<List<Categoria>> getAllCategorias(String valueFilter);

    ResponseEntity<String> updateCategoria(Map<String,String> requestMap);
}
