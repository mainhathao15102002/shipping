package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.RefreshTokenAuthReq;
import com.sb.shippingbackend.dto.request.SignInAuthReq;
import com.sb.shippingbackend.dto.request.SignUpAuthReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.Company;
import com.sb.shippingbackend.entity.Customer;
import com.sb.shippingbackend.entity.NormalCustomer;
import com.sb.shippingbackend.entity.User;
import com.sb.shippingbackend.repository.CompanyRepository;
import com.sb.shippingbackend.repository.CustomerRepository;
import com.sb.shippingbackend.repository.NormalCustomerRepository;
import com.sb.shippingbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private NormalCustomerRepository normalRepository;

    @Transactional
    public ReqRes signUp(SignUpAuthReq registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setMessage("Email is exists!");
                resp.setStatusCode(400);
                return resp;
            }
            User user = new User();
            System.out.println(user.getId());
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(registrationRequest.getRole());
            User userResult = userRepository.save(user);

            Customer customer = new Customer();
            customer.setName(registrationRequest.getName());
            customer.setPhoneNumber(registrationRequest.getPhoneNumber());
            customer.setUser(user);
            customerRepository.save(customer);
            if(registrationRequest.getIdCode() != null)
            {
                NormalCustomer normalCustomer = new NormalCustomer();
                normalCustomer.setId(customer.getId());
                normalCustomer.setIdCode(registrationRequest.getIdCode());
                normalRepository.save(normalCustomer);
            }
            else {
                Company company = new Company();
                company.setId(customer.getId());
                company.setTaxCode(registrationRequest.getTaxCode());
                companyRepository.save(company);
            }
            if(userResult.getId() > 0) {
                resp.setUser(userResult);
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
            }
        }catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }
    public ReqRes signIn(SignInAuthReq signInAuthRequest) {
        ReqRes resp = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInAuthRequest.getEmail(),signInAuthRequest.getPassword()));
            var user = userRepository.findByEmail(signInAuthRequest.getEmail()).orElseThrow();
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
    public ReqRes refreshToken(RefreshTokenAuthReq refreshRequest) {
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
