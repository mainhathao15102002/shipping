package com.sb.shippingbackend.repository;

import com.sb.shippingbackend.entity.InternalShipping;
import com.sb.shippingbackend.entity.InternalShippingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InternalShippingDetailRepository extends JpaRepository<InternalShippingDetail, String> {
    @Query("SELECT i FROM InternalShipping i WHERE (:postOfficeId = i.postOfficeSend.id OR " +
            "i.listPostOffice LIKE CONCAT('%-', :postOfficeId, '-%') OR " +
            "i.listPostOffice LIKE CONCAT(:postOfficeId, '-%') OR " +
            "i.listPostOffice LIKE CONCAT('%-', :postOfficeId)) AND" +
            "(i.listPostOfficeCompleted NOT LIKE CONCAT('%-', :postOfficeId, '-%') AND " +
            "i.listPostOfficeCompleted NOT LIKE CONCAT(:postOfficeId, '-%') AND " +
            "i.listPostOfficeCompleted NOT LIKE CONCAT('%-', :postOfficeId)) ")
    List<InternalShipping> findByPostOfficeId(Integer postOfficeId);
}
