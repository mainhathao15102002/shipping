package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.service.LogService;
import com.sb.shippingbackend.service.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/log")
public class LogController {
    @Autowired
    private LogService logService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestParam(required = false)  String table,@RequestParam(required = false)  String id ,@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(logService.getAllByPostOfficeId(jwtToken,table, id));
    }

}
