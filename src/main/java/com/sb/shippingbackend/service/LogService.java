package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.LogActionReq;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.dto.response.LogActionRes;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.Employee;
import com.sb.shippingbackend.entity.InternalShipping;
import com.sb.shippingbackend.entity.Log;
import com.sb.shippingbackend.entity.User;
import com.sb.shippingbackend.repository.EmployeeRepository;
import com.sb.shippingbackend.repository.LogRepository;
import com.sb.shippingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LogService {
    @Autowired
    private LogRepository logRepository;

    @Autowired
    private JWTUtils jwtUtil;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public void logAction(String action, String table, String idObject, String token) {
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email).orElseThrow(null);
            Log log = new Log();
            log.setUser(user);
            log.setAction(action);
            log.setTable(table);
            log.setIdObject(idObject);
            logRepository.save(log);
        } catch (Exception e) {
            throw new RuntimeException("Error logging action: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public LogActionRes getAllByPostOfficeId(String token) {
        LogActionRes resp = new LogActionRes();
        try {
            String username = jwtUtil.extractUsername(token);
            Employee employee = employeeRepository.findByUserEmail(username);
            if (employee != null && employee.getPostOffice() != null) {
                Integer postOfficeId = employee.getPostOffice().getId();
                List<Log> logList = logRepository.findByPostOfficeId(postOfficeId);
                resp.setLogList(logList);
                resp.setMessage("SUCCESSFUL!");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(404);
                resp.setError("PostOffice not found for the user.");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
