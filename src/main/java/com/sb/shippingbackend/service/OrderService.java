package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.CreateOrderReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateOrderReq;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MerchandisRepository merchandisRepository;

    @Autowired
    private ListPropOfMerchRepository listPropOfMerchRepository;

    @Autowired
    private SpecicalPropRepository specicalPropRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public ReqRes createOrder(CreateOrderReq createRequest) {
        ReqRes resp = new ReqRes();
        try {
            Order order = new Order();
            Optional<Customer> optionalCustomer = customerRepository.findById(createRequest.getCustomerId());;
            order.setReceiverName(createRequest.getReceiverName());
            order.setReceiverAddress(createRequest.getReceiverAddress());
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDateTime = currentDateTime.format(formatter);
            order.setCreatedDate(LocalDate.parse(formattedDateTime));
            order.setNote(createRequest.getNote());
            order.setDeliverMethod(createRequest.getDeliverMethod());
            order.setReceiverPhone(createRequest.getReceiverPhone());
            order.setTotalWeight(createRequest.getTotalWeight());
            optionalCustomer.ifPresent(order::setCustomer);

            Order orderResult = orderRepository.save(order);
            createRequest.getMerchandiseList().forEach((item) ->
            {
                Merchandise merchandise = new Merchandise();
                merchandise.setDesc(item.getDesc());
                merchandise.setSize(item.getSize());
                merchandise.setValue(item.getValue());
                merchandise.setWeight(item.getWeight());
                merchandise.setImageUrl(item.getImageUrl());
                merchandise.setQuantity(item.getQuantity());
                merchandise.setOrder(order);
                List<ListSpecicalPropOfMerchandise> list = new ArrayList<>();
                for(int i = 0; i < item.getList().size(); i++)
                {
                    ListSpecicalPropOfMerchandise listSpecicalPropOfMerchandise = new ListSpecicalPropOfMerchandise();
                    PropOfMerchId propOfMerchId = new PropOfMerchId();
                    propOfMerchId.setMerchandiseId(merchandise.getId());
                    Integer propId = item.getList().get(i).getPropOfMerchId().getPropId();
                    propOfMerchId.setPropId(propId);
                    SpecialProps specialProps = specicalPropRepository.findById(propId).orElse(null);
                    if(specialProps != null) {
                        listSpecicalPropOfMerchandise.setPropOfMerchId(propOfMerchId);
                        listSpecicalPropOfMerchandise.setSpecialProps(specialProps);
                        listSpecicalPropOfMerchandise.setMerchandise(merchandise);
                    }
                    list.add(listSpecicalPropOfMerchandise);
                }
                merchandise.setList(list);
                merchandisRepository.save(merchandise);
                listPropOfMerchRepository.saveAll(list);
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

    @Transactional
    public ReqRes updateStatusOrder(UpdateOrderReq updateRequest) {
        ReqRes resp = new ReqRes();
        try {
            String updatedId = updateRequest.getOrderId();
            if (updatedId != null && !updatedId.isEmpty()) {
                Order order = orderRepository.findById(updatedId).orElseThrow(null);
                if(order!=null)
                {
                    OrderStatus orderStatus = OrderStatus.valueOf(updateRequest.getStatus());
                    order.setStatus(orderStatus);
                    Order orderResult = orderRepository.save(order);
                    resp.setMessage("Order status updated successfully!");
                    resp.setStatusCode(200);
                    resp.setOrder(orderResult);
                }
                else {
                    resp.setMessage("NOT FOUND!");
                    resp.setStatusCode(404);
                }

            } else {
                resp.setMessage("ERROR ORDERID");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public List<Order> getAllOrder() {
        return orderRepository.findAllOrder();
    }

    public ReqRes findOrdersByCustomerId(String customerId) {
        ReqRes resp = new ReqRes();
        try {
            List<Order> orders = orderRepository.findByCustomerId(customerId);
            if (orders != null) {
                resp.setOrderList(orders);
                resp.setMessage("Order found successfully!");
                resp.setStatusCode(200);
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


    public ReqRes findOrderByOrderId(String orderId) {
        ReqRes resp = new ReqRes();
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                resp.setOrder(order);
                resp.setMessage("Order found successfully!");
                resp.setStatusCode(200);
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
