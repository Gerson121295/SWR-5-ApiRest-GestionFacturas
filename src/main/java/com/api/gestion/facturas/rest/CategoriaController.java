package com.api.gestion.facturas.rest;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.pojo.Categoria;
import com.api.gestion.facturas.service.CategoriaService;
import com.api.gestion.facturas.util.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    //Agregar nueva categoria: POST: http://localhost:8081/api/v1/categoria/add  JSON: Crear: {"nombre":"Bebidas"}  Actualizar: {"id": "1", "nombre":"Muebles"}  NOTA: antes de hacer la peticion se debe hacer login y copiar el token y pegarlo en la authorization Bearer Token
    @PostMapping("/add")
    public ResponseEntity<String> agregarNuevaCategoria(@RequestBody(required = true)Map<String,String> requestMap){
        try{
            return categoriaService.addNuevaCategoria(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Mostrar categorias: GET: http://localhost:8081/api/v1/categoria/get   NOTA: Opcional:  Enviar parametro:  valueFilter=true en postman: Params: key:valueFilter y en Value:true
    @GetMapping("/get")
    public ResponseEntity<List<Categoria>> listarCategorias(@RequestParam(required = false) String valueFilter){
        try{
            return categoriaService.getAllCategorias(valueFilter);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Actualizar Categoria: POST: http://localhost:8081/api/v1/categoria/update    JSON: {"id":"1", "nombre":"Vegetales"}  Requiere enviar el token por lo tanto hacer login antes
    @PostMapping("/update")
    public ResponseEntity<String> actualizarCategoria(@RequestBody(required = true) Map<String,String> requestMap){
        try{
            return categoriaService.updateCategoria(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
