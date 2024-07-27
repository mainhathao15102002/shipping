package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.CalculateCostReq;
import com.sb.shippingbackend.dto.request.CreateOrderReq;
import com.sb.shippingbackend.dto.request.DirectPaymentReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateOrderReq;
import com.sb.shippingbackend.service.OrderService;
import com.sb.shippingbackend.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/user/order/create")
    public ResponseEntity<?> create(@RequestBody CreateOrderReq createRequest,  @RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(orderService.createOrder(createRequest, jwtToken));
    }



    @PostMapping("/v2/order/directPayment")
    public ResponseEntity<?> directPayment(@RequestBody DirectPaymentReq directPaymentReq) {
        return ResponseEntity.ok(orderService.directPayment(directPaymentReq));
    }

    @PutMapping("/v2/order/update")
    public ResponseEntity<?> update(@RequestBody UpdateOrderReq updateRequest, @RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(orderService.updateStatusOrder(updateRequest, jwtToken));
    }
    @GetMapping("/v3/order/get-cost")
    public ResponseEntity<?> getCost(
            @RequestParam Double totalWeight,
            @RequestParam Double distance,
            @RequestParam List<Integer> specialProps,
            @RequestParam int estimatedDeliveryTime,
            @RequestParam boolean isIntraProvincial) {

        CalculateCostReq calculateCostReq = new CalculateCostReq();
        calculateCostReq.setTotalWeight(totalWeight);
        calculateCostReq.setDistance(distance);
        calculateCostReq.setSpecialProps(specialProps);
        calculateCostReq.setIntraProvincial(isIntraProvincial);
        calculateCostReq.setEstimatedDeliveryTime(estimatedDeliveryTime);
        return ResponseEntity.ok(orderService.calculateCost(calculateCostReq));
    }

    @GetMapping("/v3/order/{orderId}")
    public ResponseEntity<ReqRes> findOrderById(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.findOrderByOrderId(orderId));
    }

    @GetMapping("/v2/order/get-all")
    public ResponseEntity<?> getAllOrdersSortedByCreatedDate(@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(orderService.getAllOrder(jwtToken));
    }

    @GetMapping("/v3/order/getByCustomerId/{customerId}")
    public ResponseEntity<?> findOrdersByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.findOrdersByCustomerId(customerId));
    }
}
