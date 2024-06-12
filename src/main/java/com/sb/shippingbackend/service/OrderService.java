package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.CreateOrderReq;
import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.dto.UpdateOrderReq;
import com.sb.shippingbackend.entity.Bill;
import com.sb.shippingbackend.entity.Merchandise;
import com.sb.shippingbackend.entity.Order;
import com.sb.shippingbackend.repository.BillRepository;
import com.sb.shippingbackend.repository.MerchandisRepository;
import com.sb.shippingbackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MerchandisRepository merchandisRepository;

    @Transactional
    public ReqRes createOrder(CreateOrderReq createRequest) {
        ReqRes resp = new ReqRes();
        try {
            Order order = new Order();
            order.setReceiverName(createRequest.getReceiverName());
            order.setReceiverAddress(createRequest.getReceiverAddress());
            order.setCreatedDate(createRequest.getCreatedDate());
            order.setNote(createRequest.getNote());
            order.setDeliverMethod(createRequest.getDeliverMethod());
            order.setReceiverPhone(createRequest.getReceiverPhone());
            order.setTotalWeight(createRequest.getTotalWeight());
            Order orderResult = orderRepository.save(order);

            createRequest.getMerchandiseList().forEach(item ->
            {
                Merchandise merchandise = new Merchandise();
                merchandise.setDesc(item.getDesc());
                merchandise.setSize(item.getSize());
                merchandise.setValue(item.getValue());
                merchandise.setWeight(item.getWeight());
                merchandise.setImageUrl(item.getImageUrl());
                merchandise.setOrder(order);
                merchandisRepository.save(merchandise);
            });

            Bill bill = new Bill();
            bill.setTotalCost(createRequest.getTotalCost());
            bill.setCreatedDate(createRequest.getCreatedDate());
            bill.setBillStatus(createRequest.getBillStatus());
            bill.setOrder(order);
            billRepository.save(bill);
            if(!orderResult.getId().isEmpty()) {
                resp.setOrder(orderResult);
                resp.setMerchandiseList(orderResult.getMerchandiseList());
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
            }
            else {
                resp.setMessage("Customer not found!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes updateOrder(UpdateOrderReq updateRequest) {
        ReqRes resp = new ReqRes();
        try {
            String updatedId = updateRequest.getOrderId();
            if (updatedId != null && !updatedId.isEmpty()) {
                Order order = orderRepository.findById(updatedId).orElseThrow();
                order.setReceiverName(updateRequest.getReceiverName());
                order.setReceiverAddress(updateRequest.getReceiverAddress());
                order.setCreatedDate(updateRequest.getCreatedDate());
                order.setNote(updateRequest.getNote());
                order.setDeliverMethod(updateRequest.getDeliverMethod());
                order.setReceiverPhone(updateRequest.getReceiverPhone());
                order.setTotalWeight(updateRequest.getTotalWeight());
                Order orderResult = orderRepository.save(order);

                resp.setMessage("Order updated successfully!");
                resp.setStatusCode(200);
                resp.setOrder(orderResult);

            } else {
                resp.setMessage("Order not found!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

}
