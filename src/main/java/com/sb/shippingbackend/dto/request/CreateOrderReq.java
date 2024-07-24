package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.shippingbackend.entity.Merchandise;
import com.sb.shippingbackend.repository.ListPropOfMerchRepository;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateOrderReq {
    private String receiverName;
    private String receiverAddress;
    private Double totalWeight;
    private String note;
    private String receiverPhone;
    private String deliverMethod;
    private Double totalCost;
    private int quantity;
    private String customerId;
    private List<Merchandise> merchandiseList;
    private Integer postOfficeId;
    private boolean receiveAtHome;
    private boolean receiveAtPostOffice;

}
