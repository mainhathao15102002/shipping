package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.DirectPaymentReq;
import com.sb.shippingbackend.dto.response.BillResponse;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateBillStatusReq;
import com.sb.shippingbackend.service.BillService;
import com.sb.shippingbackend.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("/v2/bill/deactived")
    public ResponseEntity<ReqRes> updateStatus(@RequestBody UpdateBillStatusReq updateRequest) {
        return ResponseEntity.ok(billService.updateStatus(updateRequest));
    }
    @GetMapping("/v3/bill/getBillByOrderId/{orderId}")
    public ResponseEntity<BillResponse> searchBillByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(billService.findBillByOrder_Id(orderId));
    }
    @GetMapping("/v3/bill/{id}")
    public ResponseEntity<BillResponse> searchBillById(@PathVariable String id) {
        return ResponseEntity.ok(billService.findBillById(id));
    }
    @GetMapping("/v3/bill/getBillByCustomerId/{customerId}")
    public ResponseEntity<?> getBillsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(billService.getBillsByCustomerId(customerId));
    }

    @GetMapping("/v2/getAllBill")
    public ResponseEntity<?> getAllBills(@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(billService.getAllBills(jwtToken));
    }

}