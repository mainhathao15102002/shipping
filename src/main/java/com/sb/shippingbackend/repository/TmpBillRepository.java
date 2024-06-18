package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Temp_bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TmpBillRepository extends JpaRepository<Temp_bill, String> {
    @Query("SELECT t FROM Temp_bill t where t.orderId = :orderId ")
    Temp_bill findByOrderId(String orderId);
}
