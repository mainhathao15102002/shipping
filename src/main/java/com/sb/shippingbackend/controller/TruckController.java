package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.service.Utils;
import com.sb.shippingbackend.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v2/truck")
public class TruckController {

    @Autowired
    private TruckService truckService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(truckService.getAll(jwtToken));
    }
}
