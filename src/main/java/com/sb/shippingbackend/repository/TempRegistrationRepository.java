package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.TempRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TempRegistrationRepository extends JpaRepository<TempRegistration, String> {
}
