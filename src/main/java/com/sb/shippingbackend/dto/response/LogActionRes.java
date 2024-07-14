package com.sb.shippingbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.sb.shippingbackend.entity.Log;
import com.sb.shippingbackend.entity.PostOffice;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogActionRes {
    private List<Log> logList;
    private String message;
    private String error;
    private int statusCode;
}
