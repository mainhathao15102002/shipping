package com.sb.shippingbackend.service;

import com.sb.shippingbackend.entity.Employee;
import com.sb.shippingbackend.entity.Truck;
import com.sb.shippingbackend.repository.EmployeeRepository;
import com.sb.shippingbackend.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Truck> getAll(String token)
    {
        String username = jwtUtils.extractUsername(token);
        Employee employee = employeeRepository.findByUserEmail(username);
        if (employee != null && employee.getPostOffice() != null) {
            Integer postOfficeId = employee.getPostOffice().getId();
            return truckRepository.findAllByPostOfficeId(postOfficeId);
        }
        return null;
    }
}
