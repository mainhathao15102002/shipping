package com.sb.shippingbackend.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@Table(name = "buucuc")
@ToString(exclude = {"employeeList","internalShippingList","orderList","truckList"})
@JsonIgnoreProperties({"employeeList", "internalShippingList","orderList","truckList"})
public class PostOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mabuucuc")
    private Integer id;

    @Column(name ="tenbuucuc")
    private String name;

    @Column(name ="trangthai")
    private int status;

    @Column(name = "sodienthoai")
    private String phoneNumber;

    @Column(name = "diachi")
    private String address;

    @OneToMany(mappedBy = "postOffice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Employee> employeeList;

    @OneToMany(mappedBy = "postOffice")
    @JsonIgnore
    private List<InternalShippingDetail> internalShippingList;

    @OneToMany(mappedBy = "postOffice")
    @JsonIgnore
    private List<Truck> truckList;

    @OneToMany(mappedBy = "postOffice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Order> orderList;
}
