package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Customer;
import com.sb.shippingbackend.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByUserId(Integer userId);

    @Query("SELECT c FROM Customer c WHERE c.user.email = :email")
    Customer findByUserEmail(String email);
}
