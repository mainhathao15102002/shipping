package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.Bill;
import com.sb.shippingbackend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByUserId(Integer userId);

}
