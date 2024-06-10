package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "hoadon")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mahoadon")
    private String id;

    @Column(name = "tongtien")
    private Double totalCost;

    @Column(name = "ngaylap")
    private LocalDate createdDate;

    @Column(name = "trangthai")
    private byte billStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "mavandon")

    private Order order;
}
