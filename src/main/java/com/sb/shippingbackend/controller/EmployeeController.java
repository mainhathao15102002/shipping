package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.entity.Employee;
import com.sb.shippingbackend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/get-all")
    public ResponseEntity<List<Employee>> getAllEmployeesByPostOffice(@RequestHeader("Authorization") String token) {
        final String jwtToken;
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(500).body(null);
        }
        jwtToken = token.substring(7);
        List<Employee> employees = employeeService.getAllEmployeesByPostOffice(jwtToken);
        if (employees != null) {
            return ResponseEntity.ok(employees);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }
}
