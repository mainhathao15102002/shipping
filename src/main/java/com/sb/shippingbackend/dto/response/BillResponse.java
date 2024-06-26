package com.sb.shippingbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillResponse {
    private String billId;
    private String billStatus;
    private LocalDate createdDate;
    private Double totalCost;
    private String orderId;
}
