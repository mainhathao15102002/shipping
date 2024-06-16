package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "khachhang_diachi")
@ToString(exclude = "customer")
public class Address {
    @EmbeddedId
    private AdressId addressId;

    @Column(name = "sdtnguoigui")
    private String phoneNumber;

    @Column(name = "tennguoigui")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("id")
    @JoinColumn(name = "ma", referencedColumnName = "ma")
    @JsonBackReference
    private Customer customer;

}
