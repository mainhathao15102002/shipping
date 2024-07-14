package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.LogActionReq;
import com.sb.shippingbackend.dto.response.LogActionRes;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.Log;
import com.sb.shippingbackend.entity.User;
import com.sb.shippingbackend.repository.LogRepository;
import com.sb.shippingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private LogRepository logRepository;

    @Autowired
    private JWTUtils jwtUtil;

    @Autowired
    private UserRepository userRepository;

    public ReqRes logAction(LogActionReq logActionReq, String token) {
        ReqRes resp = new ReqRes();
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
}
