package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "phieuvanchuyenchokhach")
public class CustomerShipping {
    @Id
    @Column(name ="maphieukhach")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name ="ngaylap")
    private LocalDate createdDate;

    @Column(name ="ngaydukiengiao")
    private LocalDate estimatedDate;

    @Column(name = "soxe")
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai")
    private CustomerShippingStatus status = CustomerShippingStatus.PENDING;

    @OneToOne(mappedBy = "customerShipping")
    private CustomerShippingDetail customerShippingDetail;
}
