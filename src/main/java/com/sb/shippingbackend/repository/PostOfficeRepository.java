package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.PostOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostOfficeRepository extends JpaRepository<PostOffice, String> {
//
//    @Query("SELECT p FROM PostOffice p WHERE " +
//            "(:address IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
//            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
//            "(:code IS NULL OR p.id = :code)")
//    List<PostOffice> searchPostOffice(@Param("address") String address,
//                                      @Param("name") String name,
//                                      @Param("code") String code);
}
