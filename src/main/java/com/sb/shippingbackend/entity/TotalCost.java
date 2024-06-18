package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "tongtien")
@ToString(exclude = {"order","bill"})
public class TotalCost {

    @EmbeddedId
    private TotalCostId id;

    @Column(name = "tongtien")
    private Double totalCost;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "mavandon", insertable = false, updatable = false)
    private Order order;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinColumn(name = "mahoadon", insertable = false, updatable = false)
    private Bill bill;


}
