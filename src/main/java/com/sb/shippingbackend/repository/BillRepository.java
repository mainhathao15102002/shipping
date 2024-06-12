package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.dto.response.BillResponse;
import com.sb.shippingbackend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BillRepository extends JpaRepository<Bill, String> {
    Bill findBillByOrder_Id(String orderId);
}
