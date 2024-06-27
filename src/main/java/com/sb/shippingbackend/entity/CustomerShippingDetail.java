package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


@Entity
@Data
@Table(name = "bangchitietphieugiaochokhach")
public class CustomerShippingDetail {
    @Id
    @Column(name = "maphieukhach")
    private String id;

    @OneToOne
    @MapsId
    @JsonIgnore
    @JoinColumn(name = "maphieukhach")
    private CustomerShipping customerShipping;

    @ManyToOne
    @JoinColumn(name = "mabuucuc", nullable = false)
    @JsonIgnore
    private PostOffice postOffice;

    @Column(name = "ngaygiao")
    private LocalDate completedDate;

    @OneToMany(mappedBy = "customerShippingDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orderList;
}
