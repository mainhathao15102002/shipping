package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.service.SpecialPropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/adminuser/specialProps")
public class SpecialPropController {

    @Autowired
    private SpecialPropService specialPropService;

    @GetMapping("/getAll")
    public ResponseEntity<?> searchBillByOrderId() {
        return ResponseEntity.ok(specialPropService.getAll());
    }
}
