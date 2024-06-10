package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "vandon")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mavandon")
    private String id;

    @Column(name = "tennguoinhan")
    private String receiverName;

    @Column(name = "diachinguoinhan")
    private String receiverAddress;

    @Column(name = "ngaylap")
    private LocalDate createdDate;

    @Column(name = "tongtrongluong")
    private Double totalWeight;

    @Column(name = "ghichu")
    private String note;

    @Column(name = "sodienthoainguoinhan")
    private String receiverPhone;

    @Column(name = "phuongthucgiaohang")
    private String deliverMethod;

    @OneToOne(mappedBy = "order")
    @JsonIgnore
    private Bill bill;
}
