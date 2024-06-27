package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.CustomerShipping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerShippingRepository extends JpaRepository<CustomerShipping, String> {
}
