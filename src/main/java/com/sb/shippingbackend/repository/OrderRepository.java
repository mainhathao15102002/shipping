package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o ORDER BY o.createdDate DESC")
    List<Order> findAllOrder();

    List<Order> findByCustomerId(String customerId);
}
