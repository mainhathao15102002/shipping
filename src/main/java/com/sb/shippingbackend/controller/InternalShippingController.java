package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.request.PostOfficeReq;
import com.sb.shippingbackend.service.InternalShippingDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/internalShipping")
public class InternalShippingController {
    @Autowired
    private InternalShippingDetailService internalShippingDetailService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody InternalShippingReq internalShippingReq) {
        return ResponseEntity.ok(internalShippingDetailService.create(internalShippingReq));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrderList(@RequestBody InternalShippingReq internalShippingReq) {
        return ResponseEntity.ok(internalShippingDetailService.update(internalShippingReq));
    }
}
