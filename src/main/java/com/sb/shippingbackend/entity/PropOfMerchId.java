package com.sb.shippingbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PropOfMerchId implements Serializable {
    @Column(name = "mahanghoa")
    private String merchandiseId;

    @Column(name = "matinhchat")
    private Integer propId;
}
