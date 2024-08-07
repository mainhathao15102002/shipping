package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "bangchitietvanchuyennb")
@ToString(exclude = {"postOffice"})
@JsonIgnoreProperties({"internalShipping"})
public class InternalShippingDetail {

    @Id
    @Column(name = "maphieunb")
    @JsonIgnore
    private String internalId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "maphieunb")
    private InternalShipping internalShipping;

    @Column(name = "ngaygionhapkho")
    private String warehouseDate;


    @ManyToOne
    @JoinColumn(name = "mabuucuc", nullable = false)
    @JsonIgnore
    private PostOffice postOffice;

    @OneToMany(mappedBy = "internalShippingDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList;

}
