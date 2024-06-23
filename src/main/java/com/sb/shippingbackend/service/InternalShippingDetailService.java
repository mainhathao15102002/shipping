package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.entity.InternalShipping;
import com.sb.shippingbackend.entity.InternalShippingDetail;
import com.sb.shippingbackend.entity.Order;
import com.sb.shippingbackend.entity.PostOffice;
import com.sb.shippingbackend.repository.InternalShippingDetailRepository;
import com.sb.shippingbackend.repository.InternalShippingRepository;
import com.sb.shippingbackend.repository.OrderRepository;
import com.sb.shippingbackend.repository.PostOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class InternalShippingDetailService {

    @Autowired
    private InternalShippingRepository internalShippingRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InternalShippingDetailRepository internalShippingDetailRepository;


    @Autowired
    private PostOfficeRepository postOfficeRepository;
    @Transactional
    public InternalShippingRes create(InternalShippingReq internalShippingReq) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            InternalShipping internalShipping = new InternalShipping();
            LocalDateTime now = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            internalShipping.setCreatedDate(now.toLocalDate());
            internalShipping.setDepartureDate(internalShippingReq.getDepartureDate());
            PostOffice postOfficeSend = postOfficeRepository.findById(internalShippingReq.getPostOfficeSend()).orElseThrow(null);
            PostOffice postOfficeReceive = postOfficeRepository.findById(internalShippingReq.getPostOfficeRecieve()).orElseThrow(null);
            internalShipping.setPostOfficeSend(postOfficeSend);
            internalShipping.setPostOfficeRecieve(postOfficeReceive);
            //them tuyen duong


            //
            internalShipping.setLicensePlates(internalShippingReq.getLicensePlates());
            InternalShipping internalShippingResult = internalShippingRepository.save(internalShipping);

            InternalShippingDetail internalShippingDetail = new InternalShippingDetail();
            PostOffice postOffice = postOfficeRepository.findById(internalShippingReq.getPostOfficeSend()).orElseThrow(null);
            if(postOffice != null) {
                internalShippingDetail.setPostOffice(postOffice);
            }
            internalShippingDetail.setInternalShipping(internalShippingResult);
            InternalShippingDetail internalResult = internalShippingDetailRepository.save(internalShippingDetail);

            List<String> listOrderId = internalShippingReq.getOrderIdList();
            List<Order> orders = new ArrayList<>();
            for (String orderId : listOrderId) {
                Order order = orderRepository.findById(orderId).orElseThrow(null);
                order.setInternalShippingDetail(internalResult);
                orders.add(order);
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
    public InternalShippingRes update(InternalShippingReq internalShippingReq) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            InternalShippingDetail internalShippingDetail = internalShippingDetailRepository.findById(internalShippingReq.getDetailId())
                    .orElseThrow(() -> new IllegalArgumentException("InternalShippingDetail not found: " + internalShippingReq.getDetailId()));

            List<Order> existingOrders = orderRepository.findByInternalShippingDetail(internalShippingReq.getDetailId());
            for (Order order : existingOrders) {
                order.setInternalShippingDetail(null);
            }
            orderRepository.saveAll(existingOrders);
            InternalShipping internalShipping = internalShippingDetail.getInternalShipping();
            internalShipping.setDepartureDate(internalShippingReq.getDepartureDate());

            PostOffice postOfficeSend = postOfficeRepository.findById(internalShippingReq.getPostOfficeSend())
                    .orElseThrow(() -> new IllegalArgumentException("Post office send not found"));
            PostOffice postOfficeReceive = postOfficeRepository.findById(internalShippingReq.getPostOfficeRecieve())
                    .orElseThrow(() -> new IllegalArgumentException("Post office receive not found"));

            internalShipping.setPostOfficeSend(postOfficeSend);
            internalShipping.setPostOfficeRecieve(postOfficeReceive);
            internalShipping.setLicensePlates(internalShippingReq.getLicensePlates());

            internalShippingRepository.save(internalShipping);
            internalShippingDetail.setPostOffice(postOfficeSend);
            List<String> listOrderId = internalShippingReq.getOrderIdList();
            List<Order> orders = new ArrayList<>();
            for (String orderId : listOrderId) {
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
                order.setInternalShippingDetail(internalShippingDetail);
                orders.add(order);
            }

            orderRepository.saveAll(orders);
            internalShippingDetailRepository.save(internalShippingDetail);
            resp.setMessage("UPDATE SUCCESSFUL!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


}
