package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.CustomerShippingReq;
import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.response.CustomerShippingRes;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.CustomerShippingDetailRepository;
import com.sb.shippingbackend.repository.CustomerShippingRepository;
import com.sb.shippingbackend.repository.OrderRepository;
import com.sb.shippingbackend.repository.PostOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerShippingService {
    @Autowired
    private CustomerShippingRepository customerShippingRepository;

    @Autowired
    private CustomerShippingDetailRepository customerShippingDetailRepository;

    @Autowired
    private PostOfficeRepository postOfficeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public CustomerShippingRes create(CustomerShippingReq customerShippingReq) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            CustomerShipping customerShipping = new CustomerShipping();
            LocalDateTime now = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            customerShipping.setCreatedDate(now.toLocalDate());
            customerShipping.setEstimatedDate(LocalDate.from(now.plusDays(3)));
            //them tuyen duong


            //
            customerShipping.setLicensePlate(customerShippingReq.getLicensePlates());
            CustomerShipping customerShippingResult = customerShippingRepository.save(customerShipping);

            CustomerShippingDetail customerShippingDetail = new CustomerShippingDetail();
            PostOffice postOffice = postOfficeRepository.findById(customerShippingReq.getPostOfficeId()).orElseThrow(null);
            if(postOffice != null) {
                customerShippingDetail.setPostOffice(postOffice);
            }
            customerShippingDetail.setCustomerShipping(customerShippingResult);
            CustomerShippingDetail customerShippingDetailResult = customerShippingDetailRepository.save(customerShippingDetail);

            List<String> listOrderId = customerShippingReq.getOrderIdList();
            List<Order> orders = new ArrayList<>();
            for (String orderId : listOrderId) {
                Order order = orderRepository.findById(orderId).orElseThrow(null);
                if(order.getInternalShippingDetail()==null) {
                    order.setCustomerShippingDetail(customerShippingDetailResult);
                    order.setStatus(OrderStatus.WAITING);
                    orders.add(order);
                }
                else {
                    resp.setMessage("Oder "+ order.getId() + " has been in another one!");
                    resp.setStatusCode(200);
                    return resp;
                }

            }
            orderRepository.saveAll(orders);
            resp.setMessage("SUCCESSFUL!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
