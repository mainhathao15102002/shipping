package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.NormalCustomer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NormalCustomerRepository extends JpaRepository<NormalCustomer, String> {
}
