package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.CustomerShippingReq;
import com.sb.shippingbackend.service.CustomerShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/customer-shipping")
public class CustomerShippingController {
    @Autowired
    private CustomerShippingService customerShippingService;

    @PostMapping("/create")
    public ResponseEntity<?> createCustomerShipping(@RequestBody CustomerShippingReq customerShippingReq) {
        return ResponseEntity.ok(customerShippingService.create(customerShippingReq));
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateCustomerShipping(@RequestBody CustomerShippingReq customerShippingReq) {
        return ResponseEntity.ok(customerShippingService.update(customerShippingReq));
    }

    @PutMapping("/cancel/{customerShippingId}")
    public ResponseEntity<?> cancelShipping(@PathVariable String customerShippingId) {
        return ResponseEntity.ok(customerShippingService.cancelShipping(customerShippingId));
    }


    @GetMapping("/{postOfficeId}")
    public ResponseEntity<?> findByPostOfficeId(@PathVariable Integer postOfficeId) {
        return ResponseEntity.ok(customerShippingService.getAllByPostOfficeId(postOfficeId));
    }
    @PutMapping("/start-shipping/{customerShippingId}")
    public ResponseEntity<?> startTransporting(@PathVariable String customerShippingId) {
        return ResponseEntity.ok(customerShippingService.startShipping(customerShippingId));
    }
}
