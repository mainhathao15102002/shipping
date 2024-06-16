package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.AdressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, AdressId> {
    Optional<Address> findByCustomerId(String customerId);

    List<Address> findAllByCustomerId(String customerId);

    @Query("SELECT a FROM Address a where a.addressId.id = :customerId and a.addressId.address = :address")
    Optional<Address> findByCustomerIdAndAddress(String customerId, String address);


    @Transactional
    @Modifying
    @Query("UPDATE Address a SET a.addressId.address = :newAddress WHERE a.addressId.id = :customerId AND a.addressId.address = :oldAddress")
    int updateAddress(String customerId, String oldAddress, String newAddress);

}
