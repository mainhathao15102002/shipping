package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {
}
