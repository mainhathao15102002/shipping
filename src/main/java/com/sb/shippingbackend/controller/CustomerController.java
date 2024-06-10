package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.repository.AddressRepository;
import com.sb.shippingbackend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adminuser/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @PostMapping("/addAddress")
    public ResponseEntity<ReqRes> addAddress(@RequestBody ReqRes addRequest) {
        return ResponseEntity.ok(customerService.addAddress(addRequest));
    }

    @PutMapping("/updateAddress")
    public ResponseEntity<ReqRes> updateAddress(@RequestBody ReqRes updateRequest) {
        return ResponseEntity.ok(customerService.updateAddress(updateRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<ReqRes> update(@RequestBody ReqRes updateRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(updateRequest));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ReqRes> delete(@RequestBody ReqRes deleteRequest) {
        return ResponseEntity.ok(customerService.deleteCustomer(deleteRequest));
    }
}
