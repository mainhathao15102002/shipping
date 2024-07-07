package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.Customer;
import com.sb.shippingbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    @Query("SELECT e FROM Employee e WHERE e.user.email = :email")
    Employee findByUserEmail( String email);

    @Query("SELECT e FROM Employee e WHERE e.postOffice.id = :postOfficeId")
    List<Employee> findAllByPostOfficeId(Integer postOfficeId);

    Employee findByUserId(Integer userId);

}
