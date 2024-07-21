package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.SpecialProps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpecicalPropRepository extends JpaRepository<SpecialProps, Integer> {
    @Query("SELECT s FROM SpecialProps s ")
    List<SpecialProps> findAllSpecialProps();

    @Query("SELECT s FROM SpecialProps s WHERE s.id IN :ids")
    List<SpecialProps> findSpecialPropsByIds(List<Integer> ids);
}
