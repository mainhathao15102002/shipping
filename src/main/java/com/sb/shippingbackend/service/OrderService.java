package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.CreateOrderReq;
import com.sb.shippingbackend.dto.request.DirectPaymentReq;
import com.sb.shippingbackend.dto.response.DirectPaymentRes;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    private TmpBillRepository tmpBillRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PostOfficeRepository postOfficeRepository;

    @Autowired
    private TotalCostRepository totalCostRepository;
    @Transactional
    public DirectPaymentRes directPayment(DirectPaymentReq directPaymentReq) {
        DirectPaymentRes resp = new DirectPaymentRes();
        try{
            Temp_bill tmp = tmpBillRepository.findByOrderId(directPaymentReq.getOrderId());
            if(tmp != null)
            {
                Optional<Order> order = orderRepository.findById(tmp.getOrderId());
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateTime = currentDateTime.format(formatter);
                Bill bill = new Bill();
                bill.setId(tmp.getId());
                bill.setCreatedDate(LocalDate.parse(formattedDateTime));
                bill.setBillStatus(true);
                Bill billResult = billRepository.save(bill);
                if(!billResult.getId().isEmpty())
                {
                    TotalCostId totalCostId = new TotalCostId(tmp.getOrderId(), billResult.getId());

                    TotalCost totalCost = new TotalCost();
                    totalCost.setId(totalCostId);
                    totalCost.setTotalCost(tmp.getTotalCost());
                    order.ifPresent(totalCost::setOrder);
                    totalCost.setBill(bill);
                    totalCostRepository.save(totalCost);
                    resp.setBillId(billResult.getId());
                    DateTimeFormatter formatterRes = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    resp.setCreatedDate(billResult.getCreatedDate().format(formatterRes));
                    resp.setTotalCost(totalCost.getTotalCost());
                    resp.setBillStatus(billResult.isBillStatus()?"PAID":"INACTIVE");
                    resp.setOrderId(billResult.getId());
                    resp.setMessage("COMPLETED PAYMENT!");
                    resp.setStatusCode(200);
                }
            }
            else {
                resp.setMessage("NOT FOUND");
                resp.setStatusCode(404);
            }

        }
        catch (Exception e)
        {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
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
            order.setReceiveAtHome(createRequest.isReceiveAtHome());
            optionalCustomer.ifPresent(order::setCustomer);

            String receiverAddress = createRequest.getReceiverAddress();
            Pattern pattern = Pattern.compile("#(\\d+)");
            Matcher matcher = pattern.matcher(receiverAddress);
            int postOfficeId = 0;
            if (matcher.find()) {
                postOfficeId = Integer.parseInt(matcher.group(1));
            }
            PostOffice postOffice = postOfficeRepository.findById(postOfficeId).orElseThrow(null);
            order.setPostOffice(postOffice);

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
                if(item.getList()!=null && !item.getList().isEmpty())
                {
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
                }
                merchandise.setList(list);
                merchandisRepository.save(merchandise);
                listPropOfMerchRepository.saveAll(list);
            });

            Temp_bill bill = new Temp_bill();
            bill.setTotalCost(createRequest.getTotalCost());
            bill.setCreatedDate(LocalDate.parse(formattedDateTime));
            bill.setOrderId(order.getId());
            tmpBillRepository.save(bill);
            if(!orderResult.getId().isEmpty()) {
                resp.setOrder(orderResult);
                resp.setMerchandiseList(orderResult.getMerchandiseList());
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
            }
            else {
                resp.setMessage("NOT FOUND");
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
    public List<Order> getAllOrder(String token) {
        String username = jwtUtils.extractUsername(token);
        PostOffice postOffice = postOfficeRepository.findByUsername(username);
        return orderRepository.findByPostOfficeId(postOffice.getId());
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
