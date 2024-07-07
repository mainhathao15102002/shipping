package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.CustomerShippingReq;
import com.sb.shippingbackend.service.CustomerShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/customer-shipping")
public class CustomerShippingController {
    @Autowired
    private CustomerShippingService customerShippingService;

    @PostMapping("/create")
    public ResponseEntity<?> createCustomerShipping(@RequestBody CustomerShippingReq customerShippingReq,@RequestHeader("Authorization") String token) {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(500).body(null);
        }
        jwtToken = token.substring(7);
        return ResponseEntity.ok(customerShippingService.create(customerShippingReq, jwtToken));
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateCustomerShipping(@RequestBody CustomerShippingReq customerShippingReq, @RequestHeader("Authorization") String token) {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(500).body(null);
        }
        jwtToken = token.substring(7);
        return ResponseEntity.ok(customerShippingService.update(customerShippingReq, jwtToken));
    }

    @PutMapping("/cancel/{customerShippingId}")
    public ResponseEntity<?> cancelShipping(@PathVariable String customerShippingId) {
        return ResponseEntity.ok(customerShippingService.cancelShipping(customerShippingId));
    }


    @GetMapping("/get-all")
    public ResponseEntity<?> findByPostOfficeId(@RequestHeader("Authorization") String token) {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(500).body(null);
        }
        jwtToken = token.substring(7);
        return ResponseEntity.ok(customerShippingService.getAllByPostOfficeId(jwtToken));
    }

    @PutMapping("/start-shipping/{customerShippingId}")
    public ResponseEntity<?> startTransporting(@PathVariable String customerShippingId) {
        return ResponseEntity.ok(customerShippingService.startShipping(customerShippingId));
    }
    @PutMapping("/confirm/{customerShippingId}")
    public ResponseEntity<?> confirmed(@PathVariable String customerShippingId) {
        return ResponseEntity.ok(customerShippingService.confirmedCustomerShipping(customerShippingId));
    }
}
