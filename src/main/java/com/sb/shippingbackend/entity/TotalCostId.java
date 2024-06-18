package com.sb.shippingbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class TotalCostId {
    @Column(name = "mavandon")
    private String orderId;

    @Column(name = "mahoadon")
    private String billId;

    public TotalCostId(String orderId, String billId) {
        this.orderId = orderId;
        this.billId = billId;
    }

    public TotalCostId() {

    }

}
