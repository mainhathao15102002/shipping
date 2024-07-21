package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculateCostReq {
    private Double totalWeight;
    private Double distance;
    private List<Integer> specialProps;
    private boolean isIntraProvincial;
}
