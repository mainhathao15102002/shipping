package com.sb.shippingbackend.service;

import com.sb.shippingbackend.entity.Employee;
import com.sb.shippingbackend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private JWTUtils jwtUtil;

    public List<Employee> getAllEmployeesByPostOffice(String token) {
        String username = jwtUtil.extractUsername(token);
        Employee employee = employeeRepository.findByUserEmail(username);
        if (employee != null && employee.getPostOffice() != null) {
            Integer postOfficeId = employee.getPostOffice().getId();
            return employeeRepository.findAllByPostOfficeId(postOfficeId);
        }
        return null;
    }
}
