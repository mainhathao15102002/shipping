package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.CustomerShippingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerShippingDetailRepository extends JpaRepository<CustomerShippingDetail, String> {
}
