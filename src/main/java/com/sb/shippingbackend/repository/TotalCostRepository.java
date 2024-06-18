package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Temp_bill;
import com.sb.shippingbackend.entity.TotalCost;
import com.sb.shippingbackend.entity.TotalCostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TotalCostRepository extends JpaRepository<TotalCost, TotalCostId> {
    @Query("SELECT t FROM TotalCost t where t.bill.id = :billId ")
    TotalCost findByBillId(String billId);

}
