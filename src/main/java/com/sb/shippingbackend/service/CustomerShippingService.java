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
            customerShipping.setCreatedDate(now.toLocalDate());
            customerShipping.setEstimatedDate(LocalDate.from(now.plusDays(3)));
            //them tuyen duong

            //
            customerShipping.setStatus(CustomerShippingStatus.PENDING);
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
                if(order.getCustomerShippingDetail()==null) {
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
    @Transactional
    public CustomerShippingRes update(CustomerShippingReq customerShippingReq) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            CustomerShippingDetail customerShippingDetail = customerShippingDetailRepository.findById(customerShippingReq.getId())
                    .orElseThrow(() -> new IllegalArgumentException("InternalShippingDetail not found: " + customerShippingReq.getId()));

            List<Order> existingOrders = orderRepository.findByCustomerShippingDetail(customerShippingReq.getId());
            for (Order order : existingOrders) {
                order.setCustomerShippingDetail(null);
                order.setStatus(OrderStatus.STOCKED);
            }
            orderRepository.saveAll(existingOrders);
            CustomerShipping customerShipping = customerShippingDetail.getCustomerShipping();
            if(customerShippingReq.getEstimatedDate()!=null)
            {
                customerShipping.setEstimatedDate(customerShippingReq.getEstimatedDate());
            }
            customerShipping.setLicensePlate(customerShippingReq.getLicensePlates());

            customerShippingRepository.save(customerShipping);
            PostOffice postOffice = postOfficeRepository.findById(customerShippingReq.getPostOfficeId()).orElseThrow(null);
            customerShippingDetail.setPostOffice(postOffice);
            List<String> listOrderId = customerShippingReq.getOrderIdList();
            List<Order> orders = new ArrayList<>();
            for (String orderId : listOrderId) {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
                order.setCustomerShippingDetail(customerShippingDetail);
                order.setStatus(OrderStatus.WAITING);
                orders.add(order);
            }
            orderRepository.saveAll(orders);
            customerShippingDetailRepository.save(customerShippingDetail);
            resp.setMessage("UPDATE SUCCESSFUL!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public CustomerShippingRes cancelShipping(String customerShippingId) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            CustomerShipping customerShipping = customerShippingRepository.findById(customerShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("CustomerShipping not found: " + customerShippingId));
            customerShipping.setStatus(CustomerShippingStatus.CANCELED);

            List<Order> orders = orderRepository.findByCustomerShippingDetail(customerShippingId);
            for (Order order : orders) {
                order.setStatus(OrderStatus.STOCKED);
                order.setCustomerShippingDetail(null);
            }

            customerShippingRepository.save(customerShipping);
            orderRepository.saveAll(orders);

            resp.setMessage("Shipping canceled successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
