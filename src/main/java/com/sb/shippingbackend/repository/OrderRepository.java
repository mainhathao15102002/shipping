package com.sb.shippingbackend.repository;
import com.sb.shippingbackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order o ")
    List<Order> findAllOrder();

    @Query("SELECT o FROM  Order  o where o.customer.id = :customerId order by o.createdDate DESC")
    List<Order> findByCustomerId(String customerId);

    @Query("SELECT o FROM  Order  o where o.internalShippingDetail.internalId = :detailId")
    List<Order> findByInternalShippingDetail(String detailId);

    @Query("SELECT o FROM  Order  o where o.customerShippingDetail.id = :detailId")
    List<Order> findByCustomerShippingDetail(String detailId);

//    @Query("SELECT o FROM Order o JOIN Employee e ON o. = e.id JOIN User u ON e.userId = u.id WHERE u.email = :email ORDER BY o.createdDate DESC")
//    List<Order> findAllByUserEmail( String email);

    @Query("SELECT o FROM  Order  o where o.customerShippingDetail.id = :detailId")
    Order findOneByCustomerShippingDetail(String detailId);

    @Query("SELECT o FROM Order o WHERE o.postOffice.id = :postOffice_id")
    List<Order> findByPostOfficeId(Integer postOffice_id);

    List<Order> findByPostOfficeIdIn(Collection<Integer> postOffice_id);
}
