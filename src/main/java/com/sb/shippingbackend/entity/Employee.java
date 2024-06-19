package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @JoinColumn(name = "mataikhoan")
    private User user;

}
