package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.entity.Customer;
import com.sb.shippingbackend.entity.User;
import com.sb.shippingbackend.repository.CustomerRepository;
import com.sb.shippingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthService {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setMessage("Email is exists!");
                resp.setStatusCode(400);
                return resp;
            }
            User user = new User();
            Customer customer = new Customer();
            System.out.println(user.getId());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(registrationRequest.getRole());
            User userResult = userRepository.save(user);

            customer.setName(registrationRequest.getName());
            customer.setPhoneNumber(registrationRequest.getPhoneNumber());
            customer.setUser(user);
            customerRepository.save(customer);
            if(userResult != null && userResult.getId() > 0) {
                resp.setUser(userResult);
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes signIn(ReqRes signinRequest) {
        ReqRes resp = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),signinRequest.getPassword()));
            var user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow();
            System.out.println("user is: "+ user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setExpirationTime("24Hr");
            resp.setMessage("Successful!");
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    public ReqRes refreshToken(ReqRes refreshRequest) {
        ReqRes resp = new ReqRes();
        String userEmail = jwtUtils.extractUsername(refreshRequest.getToken());
        User users = userRepository.findByEmail(userEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshRequest.getToken(), users))
        {
            var jwt = jwtUtils.generateToken(users);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshRequest.getToken());
            resp.setExpirationTime("24Hr");
            resp.setMessage("Successful!");
        }
        resp.setStatusCode(500);
        return resp;
    }
}
