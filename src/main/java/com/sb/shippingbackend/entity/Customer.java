package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "khachhang")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ma")
    private String id;

    @Column(name = "tenkhachhang")
    private String name;

    @Column(name = "sodienthoai")
    private String phoneNumber;

    @OneToMany(mappedBy = "customer")
    @JsonIgnore
    private List<Address> addresses = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mataikhoan")
    private User user;
}
