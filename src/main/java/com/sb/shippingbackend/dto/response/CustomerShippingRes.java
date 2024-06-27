package com.sb.shippingbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.shippingbackend.entity.CustomerShipping;
import com.sb.shippingbackend.entity.InternalShipping;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerShippingRes {
    private int statusCode;
    private String error;
    private String message;
    private List<CustomerShipping> customerShippingList;
}
