package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.InternalShipping;
import com.sb.shippingbackend.entity.InternalShippingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternalShippingDetailRepository extends JpaRepository<InternalShippingDetail, String> {
    @Query("SELECT i FROM InternalShipping i JOIN i.internalShippingDetail detail WHERE detail.postOffice.id = :postOfficeId")
    List<InternalShipping> findByPostOfficeId(Integer postOfficeId);
}
