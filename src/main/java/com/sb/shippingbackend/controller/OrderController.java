package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.repository.OrderRepository;
import com.sb.shippingbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ReqRes> create(@RequestBody ReqRes createRequest) {
        return ResponseEntity.ok(orderService.createOrder(createRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<ReqRes> update(@RequestBody ReqRes updateRequest) {
        return ResponseEntity.ok(orderService.updateOrder(updateRequest));
    }
}
