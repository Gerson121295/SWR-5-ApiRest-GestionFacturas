package com.api.gestion.facturas.service.impl;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.dao.ProductoDAO;
import com.api.gestion.facturas.pojo.Categoria;
import com.api.gestion.facturas.pojo.Producto;
import com.api.gestion.facturas.security.jwt.JwtFilter;
import com.api.gestion.facturas.service.ProductoService;
import com.api.gestion.facturas.util.FacturaUtils;
import com.api.gestion.facturas.wrapper.ProductoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoDAO productoDAO;

    @Autowired
    private JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNuevoProducto(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){ //valida is el usuario es role admin.
                if(validateProductoMap(requestMap,false)){ //valida si el mapa tenga los campos del producto con validateProductMap, requestMap(son datos del producto), false(validateId indica que el producto no existe por lo tanto se deba crear
                    productoDAO.save(getProductoFromMap(requestMap,false)); //obtiene los producto del mapa getProductoFromMap(request(datos del producto), false(isAdd indica que el producto no esta agregado en la BD por lo tanto se debe crear)
                    return FacturaUtils.getResponseEntity("Producto agregado con éxito",HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductoWrapper>> getAllProductos() {
        try{
            return new ResponseEntity<>(productoDAO.getAllProductos(),HttpStatus.OK);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProducto(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                if(validateProductoMap(requestMap,true)){ //validateProductoMap recibe el requestMap(y valida datos) y validateId(define si ya existe(true-se actualizara el producto) el id o no(false).
                    Optional<Producto> productoOptional = productoDAO.findById(Integer.parseInt(requestMap.get("id")));
                    if(!productoOptional.isEmpty()){ //si productoOptional no esta vacio
                        Producto producto = getProductoFromMap(requestMap,true); //getProductoFromMap recibe: requestMap(datos del producto),y un boolean: si es false(crear Producto), true(producto esta creada:Actualizar producto)
                        producto.setStatus(productoOptional.get().getStatus());
                        productoDAO.save(producto);
                        return FacturaUtils.getResponseEntity("Producto actualizado con éxito",HttpStatus.OK);
                    }
                    else{
                        return FacturaUtils.getResponseEntity("Ese producto no existe",HttpStatus.NOT_FOUND);
                    }
                }
                else{
                    return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
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

    @Override
    public ResponseEntity<String> deleteProducto(Integer id) {
        try{
            if(jwtFilter.isAdmin()){
                Optional productoOptional = productoDAO.findById(id); //productoOptional guarda el producto a eliminiar que es buscado por su ID
                if(!productoOptional.isEmpty()){ //si productoOptional no esta vacio
                    productoDAO.deleteById(id);
                    return FacturaUtils.getResponseEntity("Producto eliminado con éxito",HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity(FacturaConstantes.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{
            if(jwtFilter.isAdmin()){
                Optional productoOptional = productoDAO.findById(Integer.parseInt(requestMap.get("id")));
                if(!productoOptional.isEmpty()){
                    //se llama al metodo updateStatus el cual se el envia el status y id que son extraidos del requestMap. que se recibe.
                    productoDAO.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    return FacturaUtils.getResponseEntity("Status del producto actualizado con éxito",HttpStatus.OK);
                }
                return FacturaUtils.getResponseEntity("El producto no existe",HttpStatus.NOT_FOUND);
            }
            else{
                return FacturaUtils.getResponseEntity(FacturaConstantes.UNAUTHORIZED_ACCESS,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductoWrapper>> getByCategoria(Integer id) {
        try{
            return new ResponseEntity<>(productoDAO.getProductoByCategoria(id),HttpStatus.OK);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductoWrapper> getProductoById(Integer id) {
        try{
            return new ResponseEntity<>(productoDAO.getProductoById(id),HttpStatus.OK);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ProductoWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }


    //Obtiene el producto del mapa: Map<String,String> requestMap y lo agrega al objeto producto que se retornará
    private Producto getProductoFromMap(Map<String,String> requestMap,boolean isAdd){ //recibe el mapa(con clave valor de los datos String) y recibe un boolean isAdd(si esta Agregado), true esta agregado en BD(actualizar), false(crear uno nuevo)
        Categoria categoria = new Categoria(); //crea una instancia de la Categoria llamada categoria
        categoria.setId(Integer.parseInt(requestMap.get("categoriaId"))); //del requestMap se obtiene el categoriaId y se establece el id de la categoria

        Producto producto = new Producto(); //se crea una instancia del Producto llamado producto

        if(isAdd){ //si el isAdd=true el producto ya esta agregado, se debe actualizar por lo tanto se debe agregar el id que viene del requestMap al producto
            producto.setId(Integer.parseInt(requestMap.get("id")));
        }else{
            //si isAdd=false el producto se crea y automaticamente se le crea un id y se le agrega el estatus
            producto.setStatus("true");
        }
        //se le agregan otros datos al producto
        producto.setCategoria(categoria);
        producto.setNombre(requestMap.get("nombre")); ///obtiene el nombre del mapa y se lo agrega al objeto producto
        producto.setDescripcion(requestMap.get("descripcion"));
        producto.setPrecio(Integer.parseInt(requestMap.get("precio")));
        return producto; //retorna el producto
    }

    //Valida que el mapa tenga el nombre y si contiene id o no. si ya existe el producto (se actualizará xq (validateId=True)) si no existe(se creará xq (validateId=false))
    private boolean validateProductoMap(Map<String,String> requestMap,boolean validateId){ //recibe el mapa con los datos y validateId puede ser false: o true:
        if(requestMap.containsKey("nombre")){
            if(requestMap.containsKey("id") && validateId){
                return true;
            }
            if(!validateId){
                return true;
            }
        }
        return false; //si no contiene el campo nombre en el mapa
    }
}
