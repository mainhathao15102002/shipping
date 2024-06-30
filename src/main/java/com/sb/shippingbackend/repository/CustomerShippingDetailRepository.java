package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.CustomerShipping;
import com.sb.shippingbackend.entity.CustomerShippingDetail;
import com.sb.shippingbackend.entity.InternalShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerShippingDetailRepository extends JpaRepository<CustomerShippingDetail, String> {
    @Query("SELECT c FROM CustomerShipping c JOIN c.customerShippingDetail detail WHERE detail.postOffice.id = :postOfficeId")
    List<CustomerShipping> findByPostOfficeId(Integer postOfficeId);
}
