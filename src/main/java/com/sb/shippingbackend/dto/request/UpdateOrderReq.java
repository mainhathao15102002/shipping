package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateOrderReq {
    private String orderId;
    private String receiverName;
    private String receiverAddress;
    private Double totalWeight;
    private String note;
    private String receiverPhone;
    private String deliverMethod;
    private LocalDate createdDate;
}
