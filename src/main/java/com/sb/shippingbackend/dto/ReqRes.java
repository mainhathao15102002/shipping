package com.sb.shippingbackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.AdressId;
import com.sb.shippingbackend.entity.Order;
import com.sb.shippingbackend.entity.User;
import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReqRes
{
    private String customerId;
    private String orderId;
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String email;
    private String role;
    private String password;
    private String username;
    private String phoneNumber;
    private User user;
    private Double totalCost;
    private LocalDate createdDate;
    private byte billStatus;
    private String receiverName;
    private String receiverAddress;
    private Double totalWeight;
    private String note;
    private String receiverPhone;
    private String deliverMethod;
    private Order order;
    private String address;
    private Address addressObject;
    private AdressId adressId;

}
