package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("SELECT b FROM Bill b JOIN b.order o WHERE o.customer.id = :customerId")
    List<Bill> findAllByCustomerId(String customerId);

    Bill findBillByOrder_Id(String orderId);
}
