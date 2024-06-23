package com.sb.shippingbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "phieuvanchuyennb")
public class InternalShipping {
    @Id
    @Column(name = "maphieunb")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "ngaylapphieu")
    private LocalDate createdDate;

    @Column(name = "ngaykhoihanh")
    private LocalDate departureDate;

    @ManyToOne
    @JoinColumn(name = "mabuucucgui", referencedColumnName = "mabuucuc")
    private PostOffice postOfficeSend;

    @ManyToOne
    @JoinColumn(name = "mabuucucnhan", referencedColumnName = "mabuucuc")
    private PostOffice postOfficeRecieve;

    @Column(name = "biensoxe")
    private String licensePlates;

    @Column(name = "trangthai")
    private boolean status;


    @OneToOne(mappedBy = "internalShipping")
    private InternalShippingDetail internalShippingDetail;
}
