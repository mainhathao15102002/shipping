package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Employee;
import com.sb.shippingbackend.entity.Truck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TruckRepository extends JpaRepository<Truck, Integer> {
    @Query("SELECT t FROM Truck t WHERE t.postOffice.id = :postOfficeId")
    List<Truck> findAllByPostOfficeId(Integer postOfficeId);
}
