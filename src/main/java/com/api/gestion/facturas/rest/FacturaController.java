package com.api.gestion.facturas.rest;

import com.api.gestion.facturas.constantes.FacturaConstantes;
import com.api.gestion.facturas.pojo.Factura;
import com.api.gestion.facturas.service.FacturaService;
import com.api.gestion.facturas.util.FacturaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/factura")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    //Genera el Reporte pdf de la factura luego de enviar el POST. POST: http://localhost:8081/api/v1/factura/generarReporte  -Requiere token, por lo que necesitará hacer login antes para obtener el token.
    //JSON a enviar: {"fileName":"reporte-factura","numeroContacto":"981091672","email":"test@gmail.com","nombre":"Test","metodoPago":"Cash","productoDetalles":"[{\"id\":18,\"nombre\":\"Nombre 01\",\"categoria\":\"Coffee\",\"cantidad\":\"1\",\"precio\":120,\"total\":120},{\"id\":19,\"nombre\":\"Nombre 02\",\"categoria\":\"Coffee\",\"cantidad\":\"3\",\"precio\":220,\"total\":120},{\"id\":20,\"nombre\":\"Nombre 03\",\"categoria\":\"Coffee\",\"cantidad\":\"3\",\"precio\":120,\"total\":120},{\"id\":21,\"nombre\":\"Nombre 04\",\"categoria\":\"Coffee\",\"cantidad\":\"1\",\"precio\":120,\"total\":120}]","montoTotal":"279"}
    @PostMapping("/generarReporte")
    ResponseEntity<String> generarReporte(@RequestBody Map<String,Object> requestMap){
        try{
            return facturaService.generateReport(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Actividad: Solo Guardar factura: POST: http://localhost:8081/api/v1/factura/guardaFactura  -Enviar JSON de datos de la factura solo Guarda
    // Metodo para generar factura por su ID: http://localhost:8081/api/v1/factura/generarPDF/1

    //Genera Factura. GET: http://localhost:8081/api/v1/factura/getFacturas  -Requiere token, por lo que necesitará hacer login antes para obtener el token.
    @GetMapping("/getFacturas")
    public ResponseEntity<List<Factura>> listarFacturas(){
        try{
            return facturaService.getFacturas();
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    //Genera el PDF. POST: http://localhost:8081/api/v1/factura/getPdf  -Requiere token, por lo que necesitará hacer login antes para obtener el token.
    //JSON a enviar: {"fileName":"reporte-factura", "uuid": "FACTURA-1738963435374", "numeroContacto":"981091672","email":"test@gmail.com","nombre":"Test","metodoPago":"Cash","productoDetalles":"[{\"id\":18,\"nombre\":\"Nombre 01\",\"categoria\":\"Coffee\",\"cantidad\":\"1\",\"precio\":120,\"total\":120},{\"id\":19,\"nombre\":\"Nombre 02\",\"categoria\":\"Coffee\",\"cantidad\":\"3\",\"precio\":220,\"total\":120},{\"id\":20,\"nombre\":\"Nombre 03\",\"categoria\":\"Coffee\",\"cantidad\":\"3\",\"precio\":120,\"total\":120},{\"id\":21,\"nombre\":\"Nombre 04\",\"categoria\":\"Coffee\",\"cantidad\":\"1\",\"precio\":120,\"total\":120}]","montoTotal":"279"}   -Nota el uuid pueda que solo necesita de numeros y no de: FACTURA-
    @PostMapping("/getPdf")
    public ResponseEntity<byte[]> obtenerPDF(@RequestBody Map<String,Object> requestMap){
        try{
            return facturaService.getPdf(requestMap);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    //Elimina Factura: http://localhost:8081/api/v1/factura/delete/2  -Requiere token, por lo que necesitará hacer login antes para obtener el token.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> eliminarFactura(@PathVariable Integer id){
        try{
            return facturaService.deleteFactura(id);
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return FacturaUtils.getResponseEntity(FacturaConstantes.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
