package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "khachhang_diachi")
public class Address {
    @EmbeddedId
    private AdressId addressId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "ma", referencedColumnName = "ma")
    @JsonIgnore
    private Customer customer;

}
