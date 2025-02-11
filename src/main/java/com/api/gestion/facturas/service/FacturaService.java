package com.api.gestion.facturas.service;

import com.api.gestion.facturas.pojo.Factura;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FacturaService {
    ResponseEntity<String> generateReport(Map<String,Object> requestMap);

    ResponseEntity<List<Factura>> getFacturas();

    ResponseEntity<byte[]> getPdf(Map<String,Object> requestMap);

    ResponseEntity<String> deleteFactura(Integer id);
}
