package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Integer> {
}
