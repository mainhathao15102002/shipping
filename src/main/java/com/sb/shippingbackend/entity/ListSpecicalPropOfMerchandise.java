package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "bangtinhchathanghoa")
public class ListSpecicalPropOfMerchandise {
    @EmbeddedId
    private PropOfMerchId propOfMerchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("merchandiseId")
    @JoinColumn(name = "mahanghoa", insertable = false, updatable = false)
    private Merchandise merchandise;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("propId")
    @JoinColumn(name = "matinhchat", insertable = false, updatable = false)
    private SpecialProps specialProps;
}
