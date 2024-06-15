package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
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

    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai")
    private OrderStatus status = OrderStatus.PENDING;;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma")
    private Customer customer;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    @JsonIgnore
    private Bill bill;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Merchandise> merchandiseList;
}
