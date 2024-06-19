package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.DirectPaymentReq;
import com.sb.shippingbackend.dto.response.BillResponse;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateBillStatusReq;
import com.sb.shippingbackend.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BillController {
    @Autowired
    private BillService billService;


    @PostMapping("/admin/bill/deactived")
    public ResponseEntity<ReqRes> updateStatus(@RequestBody UpdateBillStatusReq updateRequest) {
        return ResponseEntity.ok(billService.updateStatus(updateRequest));
    }

    @GetMapping("/adminuser/bill/getBillByOrderId/{orderId}")
    public ResponseEntity<BillResponse> searchBillByOrderId(@PathVariable String orderId) {
        return ResponseEntity.ok(billService.findBillByOrder_Id(orderId));
    }
    @GetMapping("/adminuser/bill/{id}")
    public ResponseEntity<BillResponse> searchBillById(@PathVariable String billId) {
        return ResponseEntity.ok(billService.findBillById(billId));
    }
    @GetMapping("/adminuser/bill/getBillByCustomerId/{customerId}")
    public ResponseEntity<?> getBillsByCustomerId(@PathVariable String customerId) {
        return ResponseEntity.ok(billService.getBillsByCustomerId(customerId));
    }

    @GetMapping("/admin/getAllBill")
    public ResponseEntity<?> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

}