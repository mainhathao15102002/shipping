package com.sb.shippingbackend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Data
@Table(name = "temp_payment")
public class Payment {
    private String vnp_Amount;

    private String vnp_PayDate;

    private String vnp_ResponseCode;
    @Id
    private String vnp_TxnRef;
}
