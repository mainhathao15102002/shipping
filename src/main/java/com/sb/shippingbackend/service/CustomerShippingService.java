package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.CustomerShippingReq;
import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.response.CustomerShippingRes;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
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
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private JWTUtils jwtUtil;
    @Transactional
    public CustomerShippingRes create(CustomerShippingReq customerShippingReq, String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            // Giải mã token để lấy username
            String username = jwtUtil.extractUsername(token);

            // Tìm Employee dựa trên username
            Employee employee = employeeRepository.findByUserEmail(username);

            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOffice = employee.getPostOffice();

                CustomerShipping customerShipping = new CustomerShipping();
                LocalDateTime now = LocalDateTime.now();
                customerShipping.setCreatedDate(now.toLocalDate());
                customerShipping.setLicensePlate(customerShippingReq.getLicensePlates());
                CustomerShipping customerShippingResult = customerShippingRepository.save(customerShipping);

                CustomerShippingDetail customerShippingDetail = new CustomerShippingDetail();
                customerShippingDetail.setPostOffice(postOffice);
                customerShippingDetail.setCustomerShipping(customerShippingResult);
                CustomerShippingDetail customerShippingDetailResult = customerShippingDetailRepository.save(customerShippingDetail);

                if (customerShippingReq.getOrderId() != null) {
                    String orderId = customerShippingReq.getOrderId();
                    Order order = orderRepository.findById(orderId).orElseThrow(null);
                    if (order != null) {
                        if (order.getCustomerShippingDetail() == null) {
                            order.setCustomerShippingDetail(customerShippingDetailResult);
                        } else {
                            resp.setMessage("Order " + order.getId() + " has been in another one!");
                            resp.setStatusCode(200);
                            return resp;
                        }
                        orderRepository.save(order);
                    }
                }

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
    public CustomerShippingRes update(CustomerShippingReq customerShippingReq, String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            // Giải mã token để lấy username
            String username = jwtUtil.extractUsername(token);

            // Tìm Employee dựa trên username
            Employee employee = employeeRepository.findByUserEmail(username);

            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOffice = employee.getPostOffice();

                CustomerShippingDetail customerShippingDetail = customerShippingDetailRepository.findById(customerShippingReq.getId())
                        .orElseThrow(() -> new IllegalArgumentException("CustomerShippingDetail not found: " + customerShippingReq.getId()));

                List<Order> existingOrders = orderRepository.findByCustomerShippingDetail(customerShippingReq.getId());
                for (Order order : existingOrders) {
                    order.setCustomerShippingDetail(null);
                }
                orderRepository.saveAll(existingOrders);

                CustomerShipping customerShipping = customerShippingDetail.getCustomerShipping();
                if (customerShippingReq.getEstimatedDate() != null) {
                    customerShipping.setEstimatedDate(customerShippingReq.getEstimatedDate());
                }
                customerShipping.setLicensePlate(customerShippingReq.getLicensePlates());

                customerShippingRepository.save(customerShipping);
                customerShippingDetail.setPostOffice(postOffice);

                String orderId = customerShippingReq.getOrderId();
                Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
                order.setCustomerShippingDetail(customerShippingDetail);
                orderRepository.save(order);

                customerShippingDetailRepository.save(customerShippingDetail);

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
    public CustomerShippingRes getAllByPostOfficeId(String token) {   CustomerShippingRes resp = new CustomerShippingRes();
        try {
            // Giải mã token để lấy username
            String username = jwtUtil.extractUsername(token);

            // Tìm Employee dựa trên username
            Employee employee = employeeRepository.findByUserEmail(username);

            if (employee != null && employee.getPostOffice() != null) {
                // Lấy PostOfficeId từ Employee
                Integer postOfficeId = employee.getPostOffice().getId();

                // Lấy danh sách CustomerShipping thuộc PostOffice
                List<CustomerShipping> customerShippingList = customerShippingDetailRepository.findByPostOfficeId(postOfficeId);
                resp.setCustomerShippingList(customerShippingList);
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
    public CustomerShippingRes confirmedCustomerShipping(String customerShippingId) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            CustomerShipping customerShipping = customerShippingRepository.findById(customerShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer Shipping Id not found: " + customerShippingId));
            customerShipping.setStatus(CustomerShippingStatus.CONFIRMED);
            Order order = orderRepository.findOneByCustomerShippingDetail(customerShippingId);
            order.setStatus(OrderStatus.WAITING);
            customerShippingRepository.save(customerShipping);
            orderRepository.save(order);

            resp.setMessage("Successful!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    @Transactional
    public CustomerShippingRes startShipping(String customerShippingId) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            CustomerShipping customerShipping = customerShippingRepository.findById(customerShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer Shipping Id not found: " + customerShippingId));
            customerShipping.setStatus(CustomerShippingStatus.SHIPPING);
            LocalDateTime now = LocalDateTime.now();
            customerShipping.setEstimatedDate(LocalDate.from(now.plusDays(3)));
            List<Order> orders = orderRepository.findByCustomerShippingDetail(customerShippingId);
            for (Order order : orders) {
                order.setStatus(OrderStatus.SHIPPING);
            }

            customerShippingRepository.save(customerShipping);
            orderRepository.saveAll(orders);
            resp.setMessage("Shipping started successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }


}
