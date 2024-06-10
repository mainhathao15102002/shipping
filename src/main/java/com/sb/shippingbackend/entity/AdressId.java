package com.sb.shippingbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class AdressId implements Serializable {

    @Column(name = "ma")
    private String id;

    @Column(name = "diachi")
    private String address;

}
