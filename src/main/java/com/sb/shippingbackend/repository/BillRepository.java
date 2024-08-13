package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface BillRepository extends JpaRepository<Bill, String> {
    @Query("SELECT b FROM Bill b JOIN b.totalCost t WHERE t.order.customer.id = :customerId")
    List<Bill> findAllByCustomerId(String customerId);

    @Query("select t from TotalCost t join t.order where t.order.id = :orderId")
    Bill findBillByOrder_Id(String orderId);

    @Query("SELECT b FROM Bill b JOIN b.totalCost t JOIN t.order o  JOIN o.postOffice p WHERE p.id = :postOfficeId")
    List<Bill> findAll(Integer postOfficeId);
}
