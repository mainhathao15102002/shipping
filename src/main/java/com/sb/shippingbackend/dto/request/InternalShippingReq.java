package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.shippingbackend.entity.InternalShippingDetail;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternalShippingReq {
    private String detailId;
    private LocalDate departureDate;
    private Date completedDate;
    private Integer truckId;
    private List<String> orderIdList;
    private List<String> newOrderIdList;
    private Integer postOfficeSend;
    private String postOfficeList;
}
