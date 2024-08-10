package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.CustomerShippingReq;
import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.response.CustomerShippingRes;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.dto.response.ReqRes;
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
    @Autowired
    private LogService logService;

    private Employee getEmployee(String token) {
        String username = jwtUtil.extractUsername(token);
        return employeeRepository.findByUserEmail(username);
    }

    @Transactional
    public CustomerShippingRes create(CustomerShippingReq customerShippingReq, String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            Employee employee = getEmployee(token);
            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOffice = employee.getPostOffice();

                CustomerShipping customerShipping = new CustomerShipping();
                LocalDateTime now = LocalDateTime.now();
                customerShipping.setCreatedDate(now.toLocalDate());
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
                logService.logAction("CREATE", "CUSTOMER_SHIPPING", customerShippingResult.getId(), token);

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
            Employee employee = getEmployee(token);
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
                logService.logAction("UPDATE", "CUSTOMER_SHIPPING", customerShippingReq.getId(), token);

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
    public CustomerShippingRes cancelShipping(String customerShippingId, String token) {
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
            logService.logAction("CANCEL", "CUSTOMER_SHIPPING", customerShippingId, token);

            resp.setMessage("Shipping canceled successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public CustomerShippingRes getAllByPostOfficeId(String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            Employee employee = getEmployee(token);
            if (employee != null && employee.getPostOffice() != null) {
                Integer postOfficeId = employee.getPostOffice().getId();
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
    public CustomerShippingRes confirmedCustomerShipping(String customerShippingId, String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            CustomerShipping customerShipping = customerShippingRepository.findById(customerShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer Shipping Id not found: " + customerShippingId));
            customerShipping.setStatus(CustomerShippingStatus.CONFIRMED);
            Order order = orderRepository.findOneByCustomerShippingDetail(customerShippingId);
            order.setStatus(OrderStatus.WAITING);
            customerShippingRepository.save(customerShipping);
            orderRepository.save(order);
            logService.logAction("CONFIRM", "CUSTOMER_SHIPPING", customerShippingId, token);
            resp.setMessage("Successful!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes completeOrder(String id) {
        ReqRes resp = new ReqRes();
        try {
            CustomerShipping customerShipping = customerShippingRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer Shipping Id not found: " + id));
            CustomerShippingDetail customerShippingDetail = customerShippingDetailRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Customer Shipping Detail Id not found: " + id));
            customerShipping.setStatus(CustomerShippingStatus.COMPLETED);
            LocalDate now = LocalDate.now();
            customerShippingDetail.setCompletedDate(now);

            List<Order> orders = orderRepository.findByCustomerShippingDetail(id);
            for (Order order : orders) {
                order.setStatus(OrderStatus.COMPLETED);
            }
            customerShippingRepository.save(customerShipping);
            customerShippingDetailRepository.save(customerShippingDetail);
            resp.setMessage("Shipping Completed!");
            resp.setStatusCode(200);

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;

    }

    @Transactional
    public CustomerShippingRes startShipping(String customerShippingId, String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            Employee employee = getEmployee(token);
            CustomerShipping customerShipping = customerShippingRepository.findById(customerShippingId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer Shipping Id not found: " + customerShippingId));
            customerShipping.setStatus(CustomerShippingStatus.SHIPPING);
            customerShipping.setLicensePlate(employee.getUser().getEmail());
            List<Order> orders = orderRepository.findByCustomerShippingDetail(customerShippingId);
            for (Order order : orders) {
                order.setStatus(OrderStatus.SHIPPING);
            }
            customerShippingRepository.save(customerShipping);
            orderRepository.saveAll(orders);
            logService.logAction("CONFIRM_SHIPPING", "CUSTOMER_SHIPPING", customerShippingId, token);
            resp.setMessage("Shipping started successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public CustomerShippingRes getCustomerShippingByToken(String token) {
        CustomerShippingRes resp = new CustomerShippingRes();
        try {
            Employee employee = getEmployee(token);
            String username = jwtUtil.extractUsername(token);
            if (employee != null && employee.getPostOffice() != null) {
                List<CustomerShipping> customerShippingList = customerShippingRepository.findByLicensePlate(username);
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
}
