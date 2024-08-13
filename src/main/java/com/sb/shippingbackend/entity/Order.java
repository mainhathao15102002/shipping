package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "vandon")
@ToString(exclude = {"bill", "customer","internalShippingDetail"})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mavandon")
    private String id;

    @Column(name = "tennguoinhan")
    private String receiverName;

    @Column(name = "diachinguoinhan")
    private String receiverAddress;

    @Column(name = "ngaylap")
    private LocalDate createdDate;

    @Column(name = "tongtrongluong")
    private Double totalWeight;

    @Column(name = "ghichu")
    private String note;

    @Column(name = "sodienthoainguoinhan")
    private String receiverPhone;

    @Column(name = "phuongthucgiaohang")
    private String deliverMethod;

    @Column(name = "nhanhangtainha")
    private Boolean receiveAtHome;

    @Column(name = "nhanhangtaibuucuc")
    private Boolean receiveAtPostOffice;

    @Column(name = "dathanhtoan")
    private Boolean isPaid = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangthai")
    private OrderStatus status = OrderStatus.PENDING;;

    @Column(name = "ngaydukiengiao")
    private LocalDate estimatedDeliveryDate;

    @ManyToOne
    @JoinColumn(name = "ma")
    @JsonIgnore
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "mabuucuc")
    @JsonIgnore
    private PostOffice postOffice;



    @OneToOne(mappedBy = "order")
    @JsonIgnore
    private TotalCost totalCost;



    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"order", "list"})
    private List<Merchandise> merchandiseList;

    @ManyToOne
    @JoinColumn(name = "maphieunb")
    @JsonIgnore
    private InternalShippingDetail internalShippingDetail;

    @ManyToOne
    @JoinColumn(name = "maphieukhach")
    @JsonIgnore
    private CustomerShippingDetail customerShippingDetail;
}
