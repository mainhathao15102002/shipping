package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.ListSpecicalPropOfMerchandise;
import com.sb.shippingbackend.entity.PropOfMerchId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListPropOfMerchRepository extends JpaRepository<ListSpecicalPropOfMerchandise, PropOfMerchId> {
}
