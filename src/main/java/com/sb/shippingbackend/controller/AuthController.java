package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.RefreshTokenAuthReq;
import com.sb.shippingbackend.dto.request.SignInAuthReq;
import com.sb.shippingbackend.dto.request.SignUpAuthReq;
import com.sb.shippingbackend.dto.request.VerificationSignUpReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.service.AuthService;
import com.sb.shippingbackend.service.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

//    @PostMapping("/signup")
//    public ResponseEntity<ReqRes> signUp(@RequestBody SignUpAuthReq signUpRequest) {
//        return ResponseEntity.ok(authService.signUp(signUpRequest));
//    }

    @PostMapping("/signup")
    public ReqRes signUp(@RequestBody SignUpAuthReq registrationRequest) {
        return authService.signUp(registrationRequest);
    }

    @PostMapping("admin/signup-employee")
    public ResponseEntity<?> signUpEmployee(@RequestBody SignUpAuthReq registrationRequest,@RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(authService.signUpAdminAccount(registrationRequest,jwtToken));
    }


    @PostMapping("/verify")
    public ReqRes verify(@RequestBody VerificationSignUpReq verificationRequest) {
        return authService.verifyAndRegister(verificationRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<ReqRes> signIn(@RequestBody SignInAuthReq signInRequest) {
        return ResponseEntity.ok(authService.signIn(signInRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody RefreshTokenAuthReq refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/checkTokenExpiry")
    public ResponseEntity<?> checkTokenExpiry(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if(authHeader == null || authHeader.isBlank()) {
            return ResponseEntity.status(500).body("NOT VALID TOKEN");
        }

        String token = authHeader.substring(7);
        boolean isExpired = authService.checkTokenExpired(token);
        if (isExpired) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);


    }

    @GetMapping("/checkTokenOrigin")
    public ResponseEntity<?> checkTokenOrigin(@RequestHeader("Authorization") String token) {
        if(token == null || token.isBlank()) {
            return ResponseEntity.status(401).body("Token is invalid");
        }
        String jwtToken = token.substring(7);
        Boolean isValid = authService.checkTokenIsValid(jwtToken);
        if (isValid) {
            return ResponseEntity.status(401).body("Token is valid");
        }
        return ResponseEntity.ok("Token is in valid");

    }


}
