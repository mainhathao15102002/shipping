package com.sb.shippingbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "congty")
public class Company {

    @Id
    @Column(name = "ma")
    private String id;

    @Column(name = "masothue")
    private String taxCode;
}
