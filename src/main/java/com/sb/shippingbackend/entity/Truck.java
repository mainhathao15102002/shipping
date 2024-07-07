package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "xetai")
@ToString(exclude = {"postOffice"})
public class Truck {
    @Id
    @Column(name = "maxe")
    private Integer id;

    @Column(name = "trangthai")
    private TruckStatus status = TruckStatus.WAITING;

    @Column(name = "biensoxe")
    private String licensePlates;

    @ManyToOne
    @JoinColumn(name = "mabuucuc")
    @JsonIgnore
    private PostOffice postOffice;

    @OneToOne(mappedBy = "truck")
    @JsonIgnore
    private InternalShipping internalShipping;

}
