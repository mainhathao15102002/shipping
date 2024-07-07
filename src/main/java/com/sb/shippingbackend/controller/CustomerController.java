package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.AddressReq;
import com.sb.shippingbackend.dto.request.UpdateCustomerReq;
import com.sb.shippingbackend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<ReqRes> getAllAddressesByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(customerService.getAllAddressesByCustomerId(customerId));
    }
    @GetMapping("/{customerId}/getAddress")
    public ReqRes getAddressByCustomerIdAndAddress(@PathVariable String customerId,@RequestParam String address) {
        return customerService.getAddressByCustomerId(customerId, address);
    }

    @DeleteMapping("/{customerId}/deleteAddress")
    public ReqRes deleteAddressByAddress(@PathVariable String customerId, @RequestBody AddressReq address) {
        return customerService.deleteAddressByAddress(customerId, address.getAddress());
    }

    @PostMapping("/addAddress")
    public ResponseEntity<ReqRes> addAddress(@RequestBody AddressReq addRequest) {
        return ResponseEntity.ok(customerService.addAddress(addRequest));
    }

    @PutMapping("/updateAddress")
    public ResponseEntity<ReqRes> updateAddress(@RequestBody AddressReq updateRequest) {
        return ResponseEntity.ok(customerService.updateAddress(updateRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<ReqRes> update(@RequestBody UpdateCustomerReq updateRequest) {
        return ResponseEntity.ok(customerService.updateCustomer(updateRequest));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ReqRes> delete(@RequestBody ReqRes deleteRequest) {
        return ResponseEntity.ok(customerService.deleteCustomer(deleteRequest));
    }

    @GetMapping("/{customerId}")
    public ReqRes getCustomerById(@PathVariable String customerId) {
        return customerService.findCustomerById(customerId);
    }
}
