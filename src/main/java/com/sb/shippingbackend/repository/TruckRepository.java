package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TruckRepository extends JpaRepository<Truck, Integer> {

}
