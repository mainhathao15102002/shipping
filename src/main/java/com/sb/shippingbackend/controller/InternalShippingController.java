package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.service.InternalShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/internalShipping")
public class InternalShippingController {
    @Autowired
    private InternalShippingService internalShippingService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody InternalShippingReq internalShippingReq) {
        return ResponseEntity.ok(internalShippingService.create(internalShippingReq));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrderList(@RequestBody InternalShippingReq internalShippingReq) {
        return ResponseEntity.ok(internalShippingService.update(internalShippingReq));
    }

    @GetMapping("/{postOfficeId}")
    public ResponseEntity<?> findByPostOfficeId(@PathVariable Integer postOfficeId) {
        return ResponseEntity.ok(internalShippingService.getAllByPostOfficeId(postOfficeId));
    }

    @PutMapping("/start-transporting/{internalShippingId}")
    public ResponseEntity<?> startTransporting(@PathVariable String internalShippingId) {
        return ResponseEntity.ok(internalShippingService.startTransporting(internalShippingId));
    }

    @PutMapping("/cancel/{internalShippingId}")
    public ResponseEntity<?> cancelShipping(@PathVariable String internalShippingId) {
        return ResponseEntity.ok(internalShippingService.cancelShipping(internalShippingId));
    }
}
