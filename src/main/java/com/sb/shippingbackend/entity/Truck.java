package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@Table(name = "xetai")
@ToString(exclude = {"postOffice","internalShipping"})
public class Truck {
    @Id
    @Column(name = "maxe")
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai")
    private TruckStatus status = TruckStatus.WAITING;

    @Column(name = "biensoxe")
    private String licensePlates;

    @ManyToOne
    @JoinColumn(name = "mabuucuc")
    @JsonIgnore
    private PostOffice postOffice;
}
