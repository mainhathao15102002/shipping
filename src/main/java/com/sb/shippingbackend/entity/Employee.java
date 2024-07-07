package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@Table(name = "nhanvien")
@ToString(exclude = {"user"})
public class Employee {
    @Id
    @Column(name = "manhanvien")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "tennhanvien")
    private String name;

    @Column(name = "sodienthoai")
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "mabuucuc")
    @JsonIgnore
    private PostOffice postOffice;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"id","password","role","tokenList"})
    @JoinColumn(name = "mataikhoan")
    private User user;

}
