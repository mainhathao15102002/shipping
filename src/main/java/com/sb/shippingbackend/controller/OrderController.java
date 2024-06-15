package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.CreateOrderReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateOrderReq;
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
    public ResponseEntity<ReqRes> create(@RequestBody CreateOrderReq createRequest) {
        return ResponseEntity.ok(orderService.createOrder(createRequest));
    }

    @PutMapping("/update")
    public ResponseEntity<ReqRes> update(@RequestBody UpdateOrderReq updateRequest) {
        return ResponseEntity.ok(orderService.updateStatusOrder(updateRequest));
    }
}
