package com.sb.shippingbackend.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateCostReq {
    private double baseCostKm;
    private double percent0km100km;
    private double percent100km500km;
    private double percent500km1000km;
    private double percent1000kmHigher;
    private double baseCostPerKg;
    private double costPerKgOver4kg;
    private double costHigher5000kgIntraProvincial;

}
