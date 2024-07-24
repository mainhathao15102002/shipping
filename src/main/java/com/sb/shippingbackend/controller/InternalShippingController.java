package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.service.InternalShippingService;
import com.sb.shippingbackend.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/internalShipping")
public class InternalShippingController {
    @Autowired
    private InternalShippingService internalShippingService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody InternalShippingReq internalShippingReq,@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.create(internalShippingReq,jwtToken));
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirm(@RequestBody InternalShippingReq internalShippingReq,@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.confirmOrders(internalShippingReq,jwtToken));
    }

    @PostMapping("/confirmed-stocked")
    public ResponseEntity<?> confirmed(@RequestBody InternalShippingReq internalShippingReq,@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.confirmOrders(internalShippingReq,jwtToken));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateOrderList(@RequestBody InternalShippingReq internalShippingReq, @RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.update(internalShippingReq,jwtToken));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> findByPostOfficeId(@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.getAllByPostOfficeId(jwtToken));
    }

    @PutMapping("/start-transporting/{internalShippingId}")
    public ResponseEntity<?> startTransporting(@PathVariable String internalShippingId, @RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.startTransporting(internalShippingId, jwtToken));
    }

    @PutMapping("/cancel/{internalShippingId}")
    public ResponseEntity<?> cancelShipping(@PathVariable String internalShippingId,@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(internalShippingService.cancelShipping(internalShippingId, jwtToken));
    }
}
