package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostOfficeReq {
    private String id;
    private String name;
    private int status;
    private String phoneNumber;
    private String address;
}
