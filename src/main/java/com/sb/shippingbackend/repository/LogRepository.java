package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.InternalShipping;
import com.sb.shippingbackend.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LogRepository extends JpaRepository<Log, Integer> {
    @Query("SELECT l FROM Log l JOIN User u on u.id = l.user.id JOIN Employee e on e.user.id = u.id where :postOfficeId = e.postOffice.id")
    List<Log> findByPostOfficeId(Integer postOfficeId);
}
