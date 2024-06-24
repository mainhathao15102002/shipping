package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "hoadon")
@ToString(exclude = {"totalcost"})
public class Bill {
    @Id
    @Column(name = "mahoadon")
    private String id;

    @Column(name = "ngaylap")
    private LocalDate createdDate;

    @Column(name = "trangthai")
    private boolean billStatus;

    @OneToOne(mappedBy = "bill")
    @JsonIgnore
    private TotalCost totalCost;

}
