package com.sb.shippingbackend.dto.response;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DirectPaymentRes {
    private String billId;
    private String createdDate;
    private Double totalCost;
    private String billStatus;
    private String orderId;
    private String message;
    private String error;
    private int statusCode;

}
