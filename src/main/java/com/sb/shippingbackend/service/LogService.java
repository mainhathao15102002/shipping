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


    public LogActionRes logAction(LogActionReq logActionReq, String token) {
        LogActionRes resp = new LogActionRes();
        try {
            String email = jwtUtil.extractUsername(token);
            User user = userRepository.findByEmail(email).orElseThrow(null);
            if(user == null) {
                resp.setMessage("ERROR!");
                resp.setStatusCode(400);
                return resp;
            }
            Log log = new Log();
            log.setUser(user);
            log.setAction(logActionReq.getAction());
            log.setTable(logActionReq.getTable());
            log.setIdObject(logActionReq.getIdObject());
            logRepository.save(log);

            resp.setMessage("Recorded!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
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
