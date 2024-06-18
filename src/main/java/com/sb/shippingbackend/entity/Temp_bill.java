package com.sb.shippingbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "temp_bill")
public class Temp_bill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bill_id")
    private String id;

    @Column(name = "total_cost")
    private Double totalCost;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "order_id")
    private String orderId;
}
