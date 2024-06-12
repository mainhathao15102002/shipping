package com.sb.shippingbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "hanghoa")
public class Merchandise {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mahanghoa")
    private String id;

    @Column(name = "mota")
    private String desc;

    @Column(name = "trongluong")
    private Double weight;

    @Column(name = "giatri")
    private Double value;

    @Column(name = "hinhanh")
    private String imageUrl;

    @Column(name = "kichthuoc")
    private String  size;

    @ManyToOne
    @JoinColumn(name = "mavandon", nullable = false)
    private Order order;

}
