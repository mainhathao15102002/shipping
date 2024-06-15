package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.RefreshTokenAuthReq;
import com.sb.shippingbackend.dto.request.SignInAuthReq;
import com.sb.shippingbackend.dto.request.SignUpAuthReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthService {


    private static class TempRegistration {
        private SignUpAuthReq registrationRequest;
        private String verificationCode;

        public TempRegistration(SignUpAuthReq registrationRequest, String verificationCode) {
            this.registrationRequest = registrationRequest;
            this.verificationCode = verificationCode;
        }

        public SignUpAuthReq getRegistrationRequest() {
            return registrationRequest;
        }

        public String getVerificationCode() {
            return verificationCode;
        }
    }


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

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    private Map<String, TempRegistration> tempRegistrations = new HashMap<>();

    @Transactional
    public ReqRes signUp(SignUpAuthReq registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setMessage("Email is exists!");
                resp.setStatusCode(400);
                return resp;
            }

            String verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationCode(registrationRequest.getEmail(), verificationCode);

            TempRegistration tempRegistration = new TempRegistration(registrationRequest, verificationCode);
            tempRegistrations.put(registrationRequest.getEmail(), tempRegistration);

            resp.setMessage("Verification code sent to email!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }

    @Transactional
    public ReqRes verifyAndRegister(String email, String code) {
        ReqRes resp = new ReqRes();
        try {
            TempRegistration tempRegistration = tempRegistrations.get(email);

            if (tempRegistration == null) {
                resp.setMessage("No registration found for this email!");
                resp.setStatusCode(404);
                return resp;
            }

            if (!tempRegistration.getVerificationCode().equals(code)) {
                resp.setMessage("Invalid verification code!");
                resp.setStatusCode(400);
                return resp;
            }

            SignUpAuthReq registrationRequest = tempRegistration.getRegistrationRequest();
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(registrationRequest.getRole());
            User userResult = userRepository.save(user);

            Customer customer = new Customer();
            customer.setName(registrationRequest.getName());
            customer.setPhoneNumber(registrationRequest.getPhoneNumber());
            customer.setUser(user);
            customerRepository.save(customer);

            if (registrationRequest.getIdCode() != null) {
                NormalCustomer normalCustomer = new NormalCustomer();
                normalCustomer.setId(customer.getId());
                normalCustomer.setIdCode(registrationRequest.getIdCode());
                normalRepository.save(normalCustomer);
            } else {
                Company company = new Company();
                company.setId(customer.getId());
                company.setTaxCode(registrationRequest.getTaxCode());
                companyRepository.save(company);
            }

            if (userResult.getId() > 0) {
                resp.setUser(userResult);
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
                tempRegistrations.remove(email); // Remove temp registration after successful registration
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }



//
//    @Transactional
//    public ReqRes signUp(SignUpAuthReq registrationRequest) {
//        ReqRes resp = new ReqRes();
//        try {
//            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
//                resp.setMessage("Email is exists!");
//                resp.setStatusCode(400);
//                return resp;
//            }
//            User user = new User();
//            System.out.println(user.getId());
//            user.setEmail(registrationRequest.getEmail());
//            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
//            user.setRole(registrationRequest.getRole());
//            User userResult = userRepository.save(user);
//
//            Customer customer = new Customer();
//            customer.setName(registrationRequest.getName());
//            customer.setPhoneNumber(registrationRequest.getPhoneNumber());
//            customer.setUser(user);
//            customerRepository.save(customer);
//            if(registrationRequest.getIdCode() != null)
//            {
//                NormalCustomer normalCustomer = new NormalCustomer();
//                normalCustomer.setId(customer.getId());
//                normalCustomer.setIdCode(registrationRequest.getIdCode());
//                normalRepository.save(normalCustomer);
//            }
//            else {
//                Company company = new Company();
//                company.setId(customer.getId());
//                company.setTaxCode(registrationRequest.getTaxCode());
//                companyRepository.save(company);
//            }
//            if(userResult.getId() > 0) {
//                resp.setUser(userResult);
//                resp.setMessage("Successful!");
//                resp.setStatusCode(200);
//            }
//        }catch (Exception e) {
//            resp.setStatusCode(500);
//            resp.setError(e.getMessage());
//            throw e;
//        }
//        return resp;
//    }
    public ReqRes signIn(SignInAuthReq signInAuthRequest) {
        ReqRes resp = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInAuthRequest.getEmail(),signInAuthRequest.getPassword()));
            var user = userRepository.findByEmail(signInAuthRequest.getEmail()).orElseThrow();
            revokeToken(user);
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            Token token = new Token();
            token.setLoggedOut(false);
            token.setToken(jwt);
            token.setUser(user);

            tokenRepository.save(token);
            resp.setStatusCode(200);
            resp.setToken(jwt);
            resp.setRefreshToken(refreshToken);
            resp.setCustomerId(customerRepository.findByUserId(user.getId()).getId());
            resp.setRole(user.getRole());
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


    private void revokeToken(User user) {
        List<Token> validTokenListByUser = tokenRepository.findAllTokenByUser(user.getId());
        if(!validTokenListByUser.isEmpty())
        {
            validTokenListByUser.forEach(t ->
                    t.setLoggedOut(true));
        }
        tokenRepository.saveAll(validTokenListByUser);
    }

    public Boolean checkTokenExpired(String token) {
        return jwtUtils.checkTokenExpired(token);
    }

    public Boolean checkTokenIsValid(String token) {
        String userEmail =  jwtUtils.extractUsername(token);
        UserDetails userDetails = userService.loadUserByUsername(userEmail);
        if (userDetails == null)
        {
            return false;
        }
        return  jwtUtils.isTokenValid(token, userDetails);
    }



}
