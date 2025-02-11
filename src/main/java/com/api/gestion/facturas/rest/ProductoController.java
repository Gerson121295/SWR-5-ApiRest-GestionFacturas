package com.api.gestion.facturas.rest;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.service.ProductoService;
import com.api.gestion.facturas.util.FacturaUtils;
import com.api.gestion.facturas.wrapper.ProductoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/producto")
@RestController
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    //Agrega un nuevo producto: POST: http://localhost:8081/api/v1/producto/add  JSON:{ "categoriaId":"2", "nombre":"Laptop HP", "descripcion":"pavillon 16pg", "precio":"1500"}  Requiere antes hacer un login para obtener token y pegarlo en Authority - BearerToken
    @PostMapping("/add")
    public ResponseEntity<String> agregarNuevoProducto(@RequestBody Map<String,String> requestMap){
        try{
            return productoService.addNuevoProducto(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Lista Productos - GET: http://localhost:8081/api/v1/producto/get  -Requiere login para obtener token y pegarlo en Authorization para ver los productos
    @GetMapping("/get")
    public ResponseEntity<List<ProductoWrapper>> listarProductos(){
        try{
            return productoService.getAllProductos();
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Actualizar todos los campos de Productos - POST: http://localhost:8081/api/v1/producto/update  JSON:{ "id": 1, "categoriaId":"2", "nombre":"Laptop Lenovo", "descripcion":"16 pulgadas", "precio":"15000"}  Requiere token
    @PostMapping("/update")
    public ResponseEntity<String> actualizarProducto(@RequestBody Map<String,String> requestMap){
        try{
            return productoService.updateProducto(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Actividad: Actualizar producto solo algunos campo. (PatchMapping) (PutMapping)
    //Variables en las rutas: PUT: http://localhost:8081/api/v1/producto/update/categoriaId/1/productoId/2
    //JSON enviar solo los datos a actualizar.


    //Eliminar producto: POST: http://localhost:8081/api/v1/producto/delete/2   -Requiere token
    @DeleteMapping("/delete/{id}")  //@PostMapping("/delete/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Integer id){
        try{
            return productoService.deleteProducto(id);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Actividad: Implementar metodo para eliminar un producto utilizando @DeleteMapping y @PathVariable


    //Actualizar producto: POST: http://localhost:8081/api/v1/producto/updateStatus  JSON:   -Requiere Token
    @PostMapping("/updateStatus")
    public ResponseEntity<String> actualizarStatus(@RequestBody Map<String,String> requestMap){
        try{
            return productoService.updateStatus(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Obtiene productos por la categoria: GET: http://localhost:8081/api/v1/producto/getByCategoria/1   -No requiere token
    @GetMapping("/getByCategoria/{id}")
    public ResponseEntity<List<ProductoWrapper>> listarProductosPorCategoria(@PathVariable Integer id){
        try{
            return productoService.getByCategoria(id);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Obtiene el producto por id: GET: http://localhost:8081/api/v1/producto/getById/1    -No requiere token
    @GetMapping("/getById/{id}")
    public ResponseEntity<ProductoWrapper> listarProductoPorId(@PathVariable Integer id){
        try{
            return productoService.getProductoById(id);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ProductoWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
