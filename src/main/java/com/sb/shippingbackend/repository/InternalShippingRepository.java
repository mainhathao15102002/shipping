package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.InternalShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternalShippingRepository extends JpaRepository<InternalShipping, String> {

}
