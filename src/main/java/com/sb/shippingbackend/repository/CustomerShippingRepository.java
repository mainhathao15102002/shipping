package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.CustomerShipping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerShippingRepository extends JpaRepository<CustomerShipping, String> {
    List<CustomerShipping> findByLicensePlate(String licensePlate);
    boolean existsByLicensePlate(String licensePlate);

}
