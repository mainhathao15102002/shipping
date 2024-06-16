package com.sb.shippingbackend.entity;

import com.sb.shippingbackend.dto.request.SignUpAuthReq;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
@Table(name = "temp_registration")
public class TempRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String email;
    private String role;
    private String phoneNumber;
    private String idCode;
    private String taxCode;
    private String password;
    private int verificationCode = 0;

}