package com.sb.shippingbackend.controller;

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

    @PostMapping("/admin/order/directPayment")
    public ResponseEntity<?> create(@RequestBody DirectPaymentReq directPaymentReq) {
        return ResponseEntity.ok(orderService.directPayment(directPaymentReq));
    }

    @PutMapping("/admin/order/update")
    public ResponseEntity<ReqRes> update(@RequestBody UpdateOrderReq updateRequest) {
        return ResponseEntity.ok(orderService.updateStatusOrder(updateRequest));
    }

    @GetMapping("/adminuser/order/{orderId}")
    public ResponseEntity<ReqRes> findOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.findOrderByOrderId(orderId));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Order>> getAllOrdersSortedByCreatedDate() {
        return ResponseEntity.ok(orderService.getAllOrder());
    }

    @GetMapping("/adminuser/order/getByCustomerId/{customerId}")
    public ResponseEntity<?> findOrdersByCustomerId(@PathVariable String customerId) {
        ResponseEntity<?> response = ResponseEntity.ok(orderService.findOrdersByCustomerId(customerId));
        return response;
    }
}
