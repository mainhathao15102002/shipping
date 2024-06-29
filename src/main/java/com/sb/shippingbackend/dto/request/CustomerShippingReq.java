package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.shippingbackend.entity.PostOffice;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerShippingReq {
    private String id;
    private LocalDate estimatedDate;
    private String licensePlates;
    private List<String> orderIdList;
    private Integer postOfficeId;
}
