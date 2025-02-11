package com.api.gestion.facturas.service.impl;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.dao.CategoriaDAO;
import com.api.gestion.facturas.pojo.Categoria;
import com.api.gestion.facturas.security.jwt.JwtFilter;
import com.api.gestion.facturas.service.CategoriaService;
import com.api.gestion.facturas.util.FacturaUtils;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaServiceImpl.class); //habilita el uso de log - la anotacion @Slf4j  no me funciona debido a que esta importada de lombok

    @Autowired
    private CategoriaDAO categoriaDAO;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNuevaCategoria(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){ //si jwtFilter es admin
                if(validateCategoriaMap(requestMap,false)){ //validateCategoriaMap recibe el requestMap y validateId(define si ya existe(true) el id o no(false)
                    categoriaDAO.save(getCategoriaFromMap(requestMap,false));  //getCategoriaFromMap recibe: requestMap(datos de la categoria),y un boolean: si es false(crear Categoria), true(categoria esta creada:Actualizar categoria)
                    return FacturaUtils.getResponseEntity("Categoría agregada con éxito", HttpStatus.OK);
                }
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Mostrar todas las categorias
    @Override
    public ResponseEntity<List<Categoria>> getAllCategorias(String valueFilter) {
        try{
            //si valueFilter no es nulo o vacio(tiene !) y valueFilter es igual a true - Requiere enviar parametro:  valueFilter=true
            if(!Strings.isNullOrEmpty(valueFilter) && valueFilter.equalsIgnoreCase("true")){
                log.info("Usando el método getAllCategorias() de Categoria - Consulta personalizada: JDBC");
                return new ResponseEntity<List<Categoria>>(categoriaDAO.getAllCategorias(),HttpStatus.OK); //retorna la lista de categoria usando el consulta personalizado con JDBC
            }
            //No requiere enviar parametro:  valueFilter=true en postman: Params: key:valueFilter y en Value:true
            log.info("Usando el método findAll() de JpaRepository");
            return new ResponseEntity<List<Categoria>>(categoriaDAO.findAll(),HttpStatus.OK); //retorna la lista de categoria usando metodo de JPA Repository
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<List<Categoria>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR); //si da error retorna una lista vacia y codigo de error
    }

    @Override
    public ResponseEntity<String> updateCategoria(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){ //si es administrador podrá actualizar la categoria
                if(validateCategoriaMap(requestMap,true)){ //metodo validateCategoriaMap recibe el requestMap y validateId(define si ya existe(true) el id o no(false)
                    Optional optional = categoriaDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(!optional.isEmpty()){ //si no esta vacio optional
                        categoriaDAO.save(getCategoriaFromMap(requestMap,true)); //getCategoriaFromMap recibe: requestMap(datos de la categoria),y un boolean: si es false(crear Categoria), true(categoria esta creada:Actualizar categoria)
                        return FacturaUtils.getResponseEntity("Categoría actualizada con éxito",HttpStatus.OK);
                    }
                    else{
                        return FacturaUtils.getResponseEntity("La categoría con ese ID no existe",HttpStatus.NOT_FOUND);
                    }
                }
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Metodo para validar que el mapa contenga el campo nombre y el id. si ya existe la categoria (se actualizará xq (validateId=True)) si no existe(se creará xq (validateId=false))
    private boolean validateCategoriaMap(Map<String,String> requestMap,boolean validateId){
        if(requestMap.containsKey("nombre")){ //si el mapa requestMap contiene la clave "nombre"
            if(requestMap.containsKey("id") && validateId){ //valida si requestMap contiene el id(categoria se actualizará) y validateId=true
                return true;  // La categoría es válida, devuelve true
            }
            if(!validateId){//si es diferente de validateId(true) o validateId es false entonces la Categoria no existe ya que ese no tiene id por lo tanto se creará
                return true;  // La categoría es válida, devuelve true
            }
        }
        // Si el mapa no contiene "nombre" o no cumple las condiciones anteriores, devuelve false
        return false; //si no contiene el campo nombre en el mapa
    }

    //Obtiene la categoria del mapa: Map<String,String> requestMap y lo agrega al objeto categoria que se retornará
    private Categoria getCategoriaFromMap(Map<String,String> requestMap,Boolean isAdd){ //recibe el mapa requestMap(con clave valor de los datos de tipo String) y recibe un boolean isAdd(si esta Agregado), true esta agregado en BD(actualizar), false(crear categoria)
        Categoria categoria = new Categoria();
        if(isAdd){ //si isAdd=true si esta agregado en la BD no se creará una nueva categoria se actualizará asignando el id que viene en el requestMap.
            categoria.setId(Integer.parseInt(requestMap.get("id"))); //se agrega el id que viene el el requestMap a la categoria porque será una actualizacion a la categoria
        }
        //no es necesario agregarle el id porque se lo agregará automaticamente
        categoria.setNombre(requestMap.get("nombre")); //se extrae el campo nombre del mapa y se agrega al objeto categoria.
        return categoria;
    }

}
