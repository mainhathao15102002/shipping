package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostOfficeRepository extends JpaRepository<PostOffice, String> {
}
