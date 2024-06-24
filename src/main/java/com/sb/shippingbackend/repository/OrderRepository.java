package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o ORDER BY o.createdDate DESC")
    List<Order> findAllOrder();

    @Query("SELECT o FROM  Order  o where o.customer.id = :customerId order by o.createdDate DESC")
    List<Order> findByCustomerId(String customerId);

    @Query("SELECT o FROM  Order  o where o.internalShippingDetail.internalId = :detailId")
    List<Order> findByInternalShippingDetail(String detailId);

}
