package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.*;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TempRegistrationRepository tempRegistrationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PostOfficeRepository postOfficeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;


//    @Cacheable(cacheNames = "tempRegistrations", key = "#email")
//    public TempRegistration getCachedRegistrationRequest(String email) {
//        return null;
//    }

//    @Cacheable(value = "tempRegistrations", key = "#email")
//    public TempRegistration cacheRegistrationRequest(String email, TempRegistration tempRegistration) {
//        return tempRegistration;
//    }

//
//    @CacheEvict(value = "tempRegistrations", key = "#email")
//    public void evictCachedRegistrationRequest(String email) {
//    }
    @Transactional
    public ReqRes resetPassword(ResetPasswordReq resetPasswordReq)
    {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(resetPasswordReq.getEmail())) {
                int verificationCode = emailService.generateVerificationCode();
                emailService.sendVerificationCode(resetPasswordReq.getEmail(), verificationCode);
                TempRegistration tempRegistration = new TempRegistration();
                tempRegistration.setEmail(resetPasswordReq.getEmail());
                tempRegistration.setPassword(passwordEncoder.encode(resetPasswordReq.getPassword()));
                tempRegistration.setVerificationCode(verificationCode);
                TempRegistration result = tempRegistrationRepository.save(tempRegistration);
                resp.setIdVerification(result.getId());
                resp.setMessage("Verification code sent to email!");
                resp.setStatusCode(200);
            }
            else {
                resp.setMessage("Email is not exists!");
                resp.setStatusCode(400);
                return resp;
            }

        }
        catch (Exception e)
        {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }
    @Transactional
    public ReqRes verifyOnResetRequest(VerificationSignUpReq verificationSignUpReq) {
        ReqRes resp = new ReqRes();
        try {
            String id = verificationSignUpReq.getId();
            int code = verificationSignUpReq.getCode();
            TempRegistration tempRegistration = tempRegistrationRepository.findById(id).orElseThrow(null);

            if (tempRegistration == null) {
                resp.setMessage("No verify found for this email!");
                resp.setStatusCode(404);
                return resp;
            }
            if (tempRegistration.getVerificationCode() != code || code == 0) {
                resp.setMessage("Invalid verification code!");
                resp.setStatusCode(400);
                return resp;
            }
            User user = userRepository.findByEmail(tempRegistration.getEmail()).orElseThrow(null);
            user.setPassword(tempRegistration.getPassword());
            User userResult = userRepository.save(user);

            if (userResult.getId() > 0) {
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
                tempRegistrationRepository.deleteById(id);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }

    @Transactional
    public ReqRes changePassword(ChangePswdReq changePswdReq, String token){
        ReqRes resp = new ReqRes();
        try {
            String username = jwtUtils.extractUsername(token);
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new Exception("User not found"));

            if (passwordEncoder.matches(changePswdReq.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoder.encode(changePswdReq.getNewPassword()));
                userRepository.save(user);
                resp.setMessage("Password changed successfully!");
                resp.setStatusCode(200);
            } else {
                resp.setMessage("Incorrect old password!");
                resp.setStatusCode(400);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes signUp(SignUpAuthReq registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setMessage("Email is exists!");
                resp.setStatusCode(400);
                return resp;
            }

            int verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationCode(registrationRequest.getEmail(), verificationCode);
            TempRegistration tempRegistration = new TempRegistration();
            tempRegistration.setEmail(registrationRequest.getEmail());
            tempRegistration.setRole(registrationRequest.getRole());
            tempRegistration.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            if (registrationRequest.getTaxCode() == null) {
                tempRegistration.setIdCode(registrationRequest.getIdCode());
            } else {
                tempRegistration.setTaxCode(registrationRequest.getTaxCode());
            }
            tempRegistration.setPhoneNumber(registrationRequest.getPhoneNumber());
            tempRegistration.setVerificationCode(verificationCode);
            tempRegistration.setName(registrationRequest.getName());
            TempRegistration result = tempRegistrationRepository.save(tempRegistration);

            resp.setIdVerification(result.getId());
            resp.setMessage("Verification code sent to email!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }



    public String generateRandomString() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int LENGTH = 6;
        final SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
    @Transactional
    public ReqRes signUpAdminAccount(SignUpAuthReq registrationRequest, String token)
    {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setMessage("Email already exists!");
                resp.setStatusCode(400);
                return resp;
            }
            String username = jwtUtils.extractUsername(token);
            User existingUser = userRepository.findByEmail(username).orElseThrow(() -> new Exception("User not found"));
            PostOffice postOffice = postOfficeRepository.findByUsername(existingUser.getUsername());
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(generateRandomString()));
            user.setRole(registrationRequest.getRole()==null?"EMPLOYEE":registrationRequest.getRole());
            User savedUser = userRepository.save(user);
            Employee employee = new Employee();
            employee.setName(registrationRequest.getName());
            employee.setPhoneNumber(registrationRequest.getPhoneNumber());
            employee.setPostOffice(postOffice);
            employee.setUser(savedUser);
            employeeRepository.save(employee);
            emailService.sendPasswordEmployee(username,registrationRequest.getPassword()    );
            resp.setMessage("Admin account created successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes signUpAccount(SignUpAuthReq registrationRequest)
    {
        ReqRes resp = new ReqRes();
        try {
            if (userRepository.existsByEmail(registrationRequest.getEmail())) {
                resp.setMessage("Email already exists!");
                resp.setStatusCode(400);
                return resp;
            }
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole("ADMIN");
            User savedUser = userRepository.save(user);
            Employee employee = new Employee();
            employee.setName(registrationRequest.getName());
            employee.setPhoneNumber(registrationRequest.getPhoneNumber());
            PostOffice postOffice = postOfficeRepository.findById(Integer.valueOf(registrationRequest.getIdCode())).orElseThrow(null);
            employee.setPostOffice(postOffice);
            employee.setUser(savedUser);
            employeeRepository.save(employee);
            resp.setMessage("Admin account created successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }



    @Transactional
    public ReqRes verifyAndRegister(VerificationSignUpReq verificationSignUpReq) {
        ReqRes resp = new ReqRes();
        try {
            String id = verificationSignUpReq.getId();
            int code = verificationSignUpReq.getCode();
            TempRegistration tempRegistration = tempRegistrationRepository.findById(id).orElseThrow(null);

            if (tempRegistration == null) {
                resp.setMessage("No registration found for this email!");
                resp.setStatusCode(404);
                return resp;
            }

            if (tempRegistration.getVerificationCode() != code || code == 0) {
                resp.setMessage("Invalid verification code!");
                resp.setStatusCode(400);
                return resp;
            }

            User user = new User();
            user.setEmail(tempRegistration.getEmail());
            user.setPassword(tempRegistration.getPassword());
            user.setRole(tempRegistration.getRole());
            User userResult = userRepository.save(user);

            Customer customer = new Customer();
            customer.setName(tempRegistration.getName());
            customer.setPhoneNumber(tempRegistration.getPhoneNumber());
            customer.setUser(user);
            customerRepository.save(customer);
            if (tempRegistration.getIdCode() != null) {
                NormalCustomer normalCustomer = new NormalCustomer();
                normalCustomer.setId(customer.getId());
                normalCustomer.setIdCode(tempRegistration.getIdCode());
                normalRepository.save(normalCustomer);
            } else {
                Company company = new Company();
                company.setId(customer.getId());
                company.setTaxCode(tempRegistration.getTaxCode());
                companyRepository.save(company);
            }

            if (userResult.getId() > 0) {
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
                tempRegistrationRepository.deleteById(id);

            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }

    public ReqRes signIn(SignInAuthReq signInAuthRequest) {
        ReqRes resp = new ReqRes();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInAuthRequest.getEmail(), signInAuthRequest.getPassword()));
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
            Customer customer = customerRepository.findByUserId(user.getId());
            if(customer != null) {
                resp.setCustomerId(customer.getId());
            }
            else
            {
                Employee employee = employeeRepository.findByUserId(user.getId());
                resp.setEmployeeId(employee.getId());
            }

            resp.setRole(user.getRole());
            resp.setExpirationTime("24Hr");
            resp.setMessage("Successful!");
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes refreshToken(RefreshTokenAuthReq refreshRequest) {
        ReqRes resp = new ReqRes();
        String userEmail = jwtUtils.extractUsername(refreshRequest.getToken());
        User users = userRepository.findByEmail(userEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshRequest.getToken(), users)) {
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
        if (!validTokenListByUser.isEmpty()) {
            validTokenListByUser.forEach(t ->
                    t.setLoggedOut(true));
        }
        tokenRepository.saveAll(validTokenListByUser);
    }

    public Boolean checkTokenExpired(String token) {
        return jwtUtils.checkTokenExpired(token);
    }

    public Boolean checkTokenIsValid(String token) {
        String userEmail = jwtUtils.extractUsername(token);
        UserDetails userDetails = userService.loadUserByUsername(userEmail);
        if (userDetails == null) {
            return false;
        }
        return jwtUtils.isTokenValid(token, userDetails);
    }
}
