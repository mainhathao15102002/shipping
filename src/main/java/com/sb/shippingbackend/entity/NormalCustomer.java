package com.sb.shippingbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "khachhangbinhthuong")
public class NormalCustomer {

    @Id
    @Column(name = "ma")
    private String id;

    @Column(name = "cccd")
    private String idCode;
}
