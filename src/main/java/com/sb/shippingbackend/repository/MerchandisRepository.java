package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Merchandise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchandisRepository extends JpaRepository<Merchandise, String> {
}
