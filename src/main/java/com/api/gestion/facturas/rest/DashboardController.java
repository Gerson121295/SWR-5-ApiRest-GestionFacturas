package com.api.gestion.facturas.rest;


import com.api.gestion.facturas.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    //Dashboard: GET: http://localhost:8081/api/v1/dashboard/detalles  -Requiere Token Retorna: Cantidad existencia de {"facturas": 1, "categorias": 3, "productos": 7}
    @GetMapping("/detalles")
    public ResponseEntity<Map<String,Object>> getCount(){
        return dashboardService.getCount();
    }

}