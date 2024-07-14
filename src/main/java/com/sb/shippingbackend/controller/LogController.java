package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.request.LogActionReq;
import com.sb.shippingbackend.repository.LogRepository;
import com.sb.shippingbackend.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/log")
public class LogController {
    @Autowired
    private LogService logService;

    @PostMapping("/record")
    public ResponseEntity<?> record(@RequestBody LogActionReq logActionReq, @RequestHeader("Authorization") String token) {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(500).body(null);
        }
        jwtToken = token.substring(7);
        return ResponseEntity.ok(logService.logAction(logActionReq,jwtToken));
    }

}
