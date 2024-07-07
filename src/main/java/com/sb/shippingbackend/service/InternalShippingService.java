package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class InternalShippingService {

    @Autowired
    private InternalShippingRepository internalShippingRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InternalShippingDetailRepository internalShippingDetailRepository;

    @Autowired
    private JWTUtils jwtUtil;

    @Autowired
    private PostOfficeRepository postOfficeRepository;
    @Transactional
    public InternalShippingRes create(InternalShippingReq internalShippingReq, String token) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            // Giải mã token để lấy username
            String username = jwtUtil.extractUsername(token);

            // Tìm Employee dựa trên username
            Employee employee = employeeRepository.findByUserEmail(username);

            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOfficeSend = employee.getPostOffice();
                PostOffice postOfficeReceive = postOfficeRepository.findById(internalShippingReq.getPostOfficeRecieve())
                        .orElseThrow(() -> new IllegalArgumentException("Post office receive not found"));

                InternalShipping internalShipping = new InternalShipping();
                LocalDateTime now = LocalDateTime.now();
                internalShipping.setCreatedDate(now.toLocalDate());
                internalShipping.setDepartureDate(internalShippingReq.getDepartureDate());
                internalShipping.setPostOfficeSend(postOfficeSend);
                internalShipping.setPostOfficeRecieve(postOfficeReceive);
                internalShipping.setLicensePlates(internalShippingReq.getLicensePlates());

                InternalShipping internalShippingResult = internalShippingRepository.save(internalShipping);

                InternalShippingDetail internalShippingDetail = new InternalShippingDetail();
                internalShippingDetail.setPostOffice(postOfficeSend);
                internalShippingDetail.setInternalShipping(internalShippingResult);
                InternalShippingDetail internalResult = internalShippingDetailRepository.save(internalShippingDetail);

                List<String> listOrderId = internalShippingReq.getOrderIdList();
                List<Order> orders = new ArrayList<>();
                for (String orderId : listOrderId) {
                    Order order = orderRepository.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
                    if (order.getInternalShippingDetail() == null) {
                        order.setInternalShippingDetail(internalResult);
                        order.setStatus(OrderStatus.PREPARING);
                        orders.add(order);
                    } else {
                        resp.setMessage("Order " + order.getId() + " has been in another one!");
                        resp.setStatusCode(200);
                        return resp;
                    }
                }
                orderRepository.saveAll(orders);
                resp.setMessage("SUCCESSFUL!");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(404);
                resp.setError("PostOffice not found for the user.");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


    @Transactional
    public InternalShippingRes update(InternalShippingReq internalShippingReq, String token) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            // Giải mã token để lấy username
            String username = jwtUtil.extractUsername(token);

            // Tìm Employee dựa trên username
            Employee employee = employeeRepository.findByUserEmail(username);

            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOfficeSend = employee.getPostOffice();
                PostOffice postOfficeReceive = postOfficeRepository.findById(internalShippingReq.getPostOfficeRecieve())
                        .orElseThrow(() -> new IllegalArgumentException("Post office receive not found"));

                InternalShippingDetail internalShippingDetail = internalShippingDetailRepository.findById(internalShippingReq.getDetailId())
                        .orElseThrow(() -> new IllegalArgumentException("InternalShippingDetail not found: " + internalShippingReq.getDetailId()));

                List<Order> existingOrders = orderRepository.findByInternalShippingDetail(internalShippingReq.getDetailId());
                for (Order order : existingOrders) {
                    order.setInternalShippingDetail(null);
                    order.setStatus(OrderStatus.CONFIRMED);
                }
                orderRepository.saveAll(existingOrders);

                InternalShipping internalShipping = internalShippingDetail.getInternalShipping();
                internalShipping.setDepartureDate(internalShippingReq.getDepartureDate());
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
                    order.setStatus(OrderStatus.PREPARING);
                    orders.add(order);
                }
                orderRepository.saveAll(orders);
                internalShippingDetailRepository.save(internalShippingDetail);

                resp.setMessage("UPDATE SUCCESSFUL!");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(404);
                resp.setError("PostOffice not found for the user.");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional(readOnly = true)
    public InternalShippingRes getAllByPostOfficeId(String token) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            String username = jwtUtil.extractUsername(token);
            Employee employee = employeeRepository.findByUserEmail(username);
            if (employee != null && employee.getPostOffice() != null) {
                Integer postOfficeId = employee.getPostOffice().getId();
                List<InternalShipping> internalShippingList = internalShippingDetailRepository.findByPostOfficeId(postOfficeId);
                resp.setInternalShippingList(internalShippingList);
                resp.setMessage("SUCCESSFUL!");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(404);
                resp.setError("PostOffice not found for the user.");
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    @Transactional
    public InternalShippingRes startTransporting(String internalShippingId) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            InternalShipping internalShipping = internalShippingRepository.findById(internalShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("InternalShipping not found: " + internalShippingId));
            internalShipping.setStatus(InternalShippingStatus.TRANSPORTING);

            List<Order> orders = orderRepository.findByInternalShippingDetail(internalShippingId);
            for (Order order : orders) {
                order.setStatus(OrderStatus.TRANSPORTING);
            }

            internalShippingRepository.save(internalShipping);
            orderRepository.saveAll(orders);

            resp.setMessage("Transporting started successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    @Transactional
    public InternalShippingRes cancelShipping(String internalShippingId) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            InternalShipping internalShipping = internalShippingRepository.findById(internalShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("InternalShipping not found: " + internalShippingId));
            internalShipping.setStatus(InternalShippingStatus.CANCELLED);

            List<Order> orders = orderRepository.findByInternalShippingDetail(internalShippingId);
            for (Order order : orders) {
                order.setStatus(OrderStatus.CONFIRMED);
                order.setInternalShippingDetail(null);
            }
            internalShippingRepository.save(internalShipping);
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
