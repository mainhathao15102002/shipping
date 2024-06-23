package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "bangchitietvanchuyennb")
public class InternalShippingDetail {

    @Id
    @Column(name = "maphieunb")
    private String internalId;  // This will act as both primary key and foreign key

    @OneToOne
    @MapsId
    @JoinColumn(name = "maphieunb")
    private InternalShipping internalShipping;

    @Column(name = "ngaygionhapkho")
    private Date warehouseDate;

    @ManyToOne
    @JoinColumn(name = "mabuucuc", nullable = false)
    private PostOffice postOffice;

    @OneToMany(mappedBy = "internalShippingDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orderList;

}
