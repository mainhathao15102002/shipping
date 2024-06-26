package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "phieuvanchuyennb")
@ToString(exclude = {"postOfficeRecieve","postOfficeSend"})
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
    @JsonIgnoreProperties({"employeeList", "internalShippingList"})
    private PostOffice postOfficeSend;

    @ManyToOne
    @JoinColumn(name = "mabuucucnhan", referencedColumnName = "mabuucuc")
    @JsonIgnoreProperties({"employeeList", "internalShippingList"})
    private PostOffice postOfficeRecieve;

    @Column(name = "biensoxe")
    private String licensePlates;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai")
    private InternalShippingStatus status = InternalShippingStatus.PENDING;

    @OneToOne(mappedBy = "internalShipping")
    @JsonIgnoreProperties({"internalShipping"})
    private InternalShippingDetail internalShippingDetail;
}
