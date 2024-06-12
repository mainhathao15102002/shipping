package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.AddressReq;
import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.dto.UpdateBillStatusReq;
import com.sb.shippingbackend.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("/admin/bill/updateStatus")
    public ResponseEntity<ReqRes> updateStatus(@RequestBody UpdateBillStatusReq updateRequest) {
        return ResponseEntity.ok(billService.updateStatus(updateRequest));
    }
}
