package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.CalculateCostReq;
import com.sb.shippingbackend.dto.request.CreateOrderReq;
import com.sb.shippingbackend.dto.request.DirectPaymentReq;
import com.sb.shippingbackend.dto.response.DirectPaymentRes;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateOrderReq;
import com.sb.shippingbackend.entity.Order;
import com.sb.shippingbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/user/order/create")
    public ResponseEntity<ReqRes> create(@RequestBody CreateOrderReq createRequest) {
        return ResponseEntity.ok(orderService.createOrder(createRequest));
    }

    @GetMapping("/v3/order/get-cost")
    public ResponseEntity<?> getCost(@RequestBody CalculateCostReq CalculateCostReq) {
        return ResponseEntity.ok(orderService.calculateCost(CalculateCostReq));
    }

    @PostMapping("/v2/order/directPayment")
    public ResponseEntity<?> directPayment(@RequestBody DirectPaymentReq directPaymentReq) {
        return ResponseEntity.ok(orderService.directPayment(directPaymentReq));
    }

    @PutMapping("/v2/order/update")
    public ResponseEntity<ReqRes> update(@RequestBody UpdateOrderReq updateRequest) {
        return ResponseEntity.ok(orderService.updateStatusOrder(updateRequest));
    }

    @GetMapping("/v3/order/{orderId}")
    public ResponseEntity<ReqRes> findOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.findOrderByOrderId(orderId));
    }

    @GetMapping("/v2/order/get-all")
    public ResponseEntity<List<Order>> getAllOrdersSortedByCreatedDate(@RequestHeader("Authorization") String token) {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(500).body(null);
        }
        jwtToken = token.substring(7);
        return ResponseEntity.ok(orderService.getAllOrder(jwtToken));
    }

    @GetMapping("/v3/order/getByCustomerId/{customerId}")
    public ResponseEntity<?> findOrdersByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.findOrdersByCustomerId(customerId));
    }
}
