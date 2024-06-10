package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.AdressId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, AdressId> {
    Optional<Address> findByCustomerId(String customerId);
}
