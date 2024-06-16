package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BillRepository extends JpaRepository<Bill, String> {
//    List<Bill> findAllByCustomerId(String customerId);
    Bill findBillByOrder_Id(String orderId);
}
