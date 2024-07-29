package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.InternalShippingReq;
import com.sb.shippingbackend.dto.response.InternalShippingRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private LogService logService;

    @Transactional
    public InternalShippingRes create(InternalShippingReq internalShippingReq, String token) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            Employee employee = getEmployee(token);
            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOfficeSend = employee.getPostOffice();
                InternalShipping internalShipping = new InternalShipping();
                LocalDateTime now = LocalDateTime.now();
                internalShipping.setCreatedDate(now.toLocalDate());
                internalShipping.setDepartureDate(internalShippingReq.getDepartureDate());
                internalShipping.setPostOfficeSend(postOfficeSend);
                Truck truck = truckRepository.findById(internalShippingReq.getTruckId()).orElseThrow(null);
                internalShipping.setTruck(truck);
                internalShipping.setListPostOffice(internalShippingReq.getPostOfficeList());
                InternalShipping internalShippingResult = internalShippingRepository.save(internalShipping);
                InternalShippingDetail internalShippingDetail = new InternalShippingDetail();
                internalShippingDetail.setPostOffice(postOfficeSend);
                internalShippingDetail.setInternalShipping(internalShippingResult);
                InternalShippingDetail internalResult = internalShippingDetailRepository.save(internalShippingDetail);
                if(internalShippingReq.getOrderIdList()!=null)
                {
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
                }
                logService.logAction("CREATE","INTERNAL_SHIPPING", internalShippingResult.getId(), token);
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
    public InternalShippingRes confirmOrders(InternalShippingReq internalShippingReq, String token) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            Employee employee = getEmployee(token);
            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOffice = employee.getPostOffice();
                String postOfficeCode = "#" + postOffice.getId();

                InternalShipping internalShipping = internalShippingRepository.findById(internalShippingReq.getDetailId())
                        .orElseThrow(() -> new IllegalArgumentException("InternalShipping not found: " + internalShippingReq.getDetailId()));
                InternalShippingDetail internalShippingDetail = internalShippingDetailRepository.findById(internalShippingReq.getDetailId()).orElseThrow(null);

                String completedPostOffices = internalShipping.getListPostOfficeCompleted();
                if (completedPostOffices != null && completedPostOffices.endsWith("-")) {
                    completedPostOffices = completedPostOffices.substring(0, completedPostOffices.length() - 1);
                }

                if (completedPostOffices != null) {
                    String[] completedPostOfficeArray = completedPostOffices.split("-");
                    if (Arrays.asList(completedPostOfficeArray).contains(postOffice.getId().toString())) {
                        resp.setMessage("PostOffice has already completed this InternalShipping!");
                        resp.setStatusCode(400);
                        return resp;
                    }
                }
                internalShipping.setListPostOfficeCompleted((completedPostOffices != null ? completedPostOffices + "-" : "") + postOffice.getId() + "-");
                String[] completedPostOfficeArray = internalShipping.getListPostOfficeCompleted().substring(0, internalShipping.getListPostOfficeCompleted().length() - 1).split("-");
                String[] listPostOfficesArray = internalShipping.getListPostOffice().split("-");
                if (completedPostOfficeArray.length == listPostOfficesArray.length) {
                    internalShipping.setStatus(InternalShippingStatus.COMPLETED);
                    internalShippingRepository.save(internalShipping);
                }
                List<Order> orders = orderRepository.findByInternalShippingDetail(internalShippingReq.getDetailId());
                List<Order> updatedOrders = new ArrayList<>();

                for (Order order : orders) {
                    if (order.getReceiverAddress() != null && order.getReceiverAddress().endsWith(postOfficeCode)) {
                        order.setStatus(OrderStatus.STOCKED);
                        order.setPostOffice(postOffice);
                        updatedOrders.add(order);
                    }
                }
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                String formattedNow = now.format(formatter);
                if(internalShippingDetail.getWarehouseDate()!=null)
                {
                    internalShippingDetail.setWarehouseDate(internalShippingDetail.getWarehouseDate()+","+formattedNow);
                }
                else {
                    internalShippingDetail.setWarehouseDate(formattedNow);

                }

                orderRepository.saveAll(updatedOrders);
                internalShippingRepository.save(internalShipping);
                logService.logAction("CONFIRM_ORDER","INTERNAL_SHIPPING", internalShippingReq.getDetailId(),token);
                resp.setMessage("Xác nhận vận đơn thành công!");
                resp.setStatusCode(200);
            } else {
                resp.setStatusCode(404);
                resp.setError("Bưu cục không tìm thấy người dùng này");
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
            Employee employee = getEmployee(token);
            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOfficeSend = employee.getPostOffice();
                InternalShippingDetail internalShippingDetail = internalShippingDetailRepository.findById(internalShippingReq.getDetailId())
                        .orElseThrow(() -> new IllegalArgumentException("InternalShippingDetail not found: " + internalShippingReq.getDetailId()));
                List<Order> existingOrders = orderRepository.findByInternalShippingDetail(internalShippingReq.getDetailId());
                for (Order order : existingOrders) {
                    order.setInternalShippingDetail(null);
                    order.setStatus(OrderStatus.CONFIRMED);
                }
                orderRepository.saveAll(existingOrders);

                InternalShipping internalShipping = internalShippingDetail.getInternalShipping();
                internalShipping.setListPostOffice(internalShippingReq.getPostOfficeList());
                internalShipping.setDepartureDate(internalShippingReq.getDepartureDate());
                internalShipping.setPostOfficeSend(postOfficeSend);
                Truck truck = truckRepository.findById(internalShippingReq.getTruckId()).orElseThrow(null);
                internalShipping.setTruck(truck);
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
                logService.logAction("UPDATE","INTERNAL_SHIPPING", internalShippingReq.getDetailId(),token);
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

    private Employee getEmployee(String token) {
        String username = jwtUtil.extractUsername(token);
        return employeeRepository.findByUserEmail(username);
    }

    @Transactional(readOnly = true)
    public InternalShippingRes getAllByPostOfficeId(String token) {
        InternalShippingRes resp = new InternalShippingRes();
        try {
            Employee employee = getEmployee(token);
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
    public InternalShippingRes startTransporting(String internalShippingId,String token) {
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
            logService.logAction("CONFIRM_SHIPPING","INTERNAL_SHIPPING", internalShippingId, token);
            resp.setMessage("Transporting started successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    @Transactional
    public InternalShippingRes cancelShipping(String internalShippingId, String token) {
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
            logService.logAction("CANCEL", "INTERNAL_SHIPPING", internalShippingId, token);
            resp.setMessage("Shipping canceled successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

}
