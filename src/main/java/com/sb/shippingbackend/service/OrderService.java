package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.CalculateCostReq;
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
    private double BASE_COST_KM = 12000.0;
    private double PERCENT_0KM_100KM = 16.5;
    private double PERCENT_100KM_500KM = 32.0;
    private double PERCENT_500KM_1000KM = 46.0;
    private double PERCENT_1000KM_HIGHER = 52.0;

    private double BASE_COST_PER_KG = 9500.0;
    private double COST_PER_KG_OVER_4KG = 5000.0;

    private double COST_HIGHER_5000KG_INTRA_PROVINCIAL = 15000.0;
    private LogRepository logRepository;
    private LogService logService;

    public Double calculateCost(CalculateCostReq calculateCostReq) {
        double totalSpecPropsCost = 0.0;
        if(!calculateCostReq.getSpecialProps().isEmpty())
        {
            List<SpecialProps> specialPropList = specicalPropRepository.findSpecialPropsByIds(calculateCostReq.getSpecialProps());
            for (SpecialProps prop : specialPropList) {
                totalSpecPropsCost += prop.getPostage();
            }
        }
        double totalweight = calculateCostReq.getTotalWeight();
        if (!calculateCostReq.isIntraProvincial()) {
            double distance = calculateCostReq.getDistance();
            double COST_PER_KM = 0.0;
            if (distance >= 40 && distance < 100) {
                COST_PER_KM = PERCENT_0KM_100KM;
            } else if (distance >= 100 && distance < 500) {
                COST_PER_KM = PERCENT_100KM_500KM;
            } else if (distance >= 500 && distance < 1000) {
                COST_PER_KM = PERCENT_500KM_1000KM;
            } else {
                COST_PER_KM = PERCENT_1000KM_HIGHER;
            }
            double COST_PER_KG = totalweight < 1000 ? 0 : totalweight / 1000 * BASE_COST_PER_KG;
            return BASE_COST_KM + (BASE_COST_KM * COST_PER_KM) / 100 + COST_PER_KG + totalSpecPropsCost;
        } else {
            if (totalweight >= 4000) {
                return COST_HIGHER_5000KG_INTRA_PROVINCIAL + totalweight / 1000 - 4000 * COST_PER_KG_OVER_4KG + totalSpecPropsCost;
            }
            return COST_HIGHER_5000KG_INTRA_PROVINCIAL;
        }
    }

    @Autowired
    private TotalCostRepository totalCostRepository;
    @Autowired
    private JWTUtils jwtUtil;

    @Transactional
    public DirectPaymentRes directPayment(DirectPaymentReq directPaymentReq) {
        DirectPaymentRes resp = new DirectPaymentRes();
        try {
            Temp_bill tmp = tmpBillRepository.findByOrderId(directPaymentReq.getOrderId());
            if (tmp != null) {
                Optional<Order> order = orderRepository.findById(tmp.getOrderId());
                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String formattedDateTime = currentDateTime.format(formatter);
                Bill bill = new Bill();
                bill.setId(tmp.getId());
                bill.setCreatedDate(LocalDate.parse(formattedDateTime));
                bill.setBillStatus(true);
                Bill billResult = billRepository.save(bill);
                if (!billResult.getId().isEmpty()) {
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
                    resp.setBillStatus(billResult.isBillStatus() ? "PAID" : "INACTIVE");
                    resp.setOrderId(billResult.getId());
                    resp.setMessage("COMPLETED PAYMENT!");
                    resp.setStatusCode(200);
                }
            } else {
                resp.setMessage("NOT FOUND");
                resp.setStatusCode(404);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
    private Customer getCustomer(String token) {
        String username = jwtUtil.extractUsername(token);
        return customerRepository.findByUserEmail(username);
    }
    @Transactional
    public ReqRes createOrder(CreateOrderReq createRequest, String token) {
        ReqRes resp = new ReqRes();
        try {
            Order order = new Order();
            Customer customer = getCustomer(token);
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
            order.setCustomer(customer);

            PostOffice postOffice = createRequest.getPostOfficeId()!=null?postOfficeRepository.findById(createRequest.getPostOfficeId()).orElseThrow(null):null;
            if(postOffice == null)
            {
                resp.setMessage("NOT FOUND POST OFFICE ID!");
                resp.setStatusCode(200);
                return resp;
            }
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
                if (item.getList() != null && !item.getList().isEmpty()) {
                    for (int i = 0; i < item.getList().size(); i++) {
                        ListSpecicalPropOfMerchandise listSpecicalPropOfMerchandise = new ListSpecicalPropOfMerchandise();
                        PropOfMerchId propOfMerchId = new PropOfMerchId();
                        propOfMerchId.setMerchandiseId(merchandise.getId());
                        Integer propId = item.getList().get(i).getPropOfMerchId().getPropId();
                        propOfMerchId.setPropId(propId);
                        SpecialProps specialProps = specicalPropRepository.findById(propId).orElse(null);
                        if (specialProps != null) {
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
            if (!orderResult.getId().isEmpty()) {
                resp.setOrder(orderResult);
                resp.setMerchandiseList(orderResult.getMerchandiseList());
                resp.setMessage("Successful!");
                resp.setStatusCode(200);
            } else {
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
    public ReqRes updateStatusOrder(UpdateOrderReq updateRequest, String token) {
        ReqRes resp = new ReqRes();
        try {
            String updatedId = updateRequest.getOrderId();
            if (updatedId != null && !updatedId.isEmpty()) {
                Order order = orderRepository.findById(updatedId).orElseThrow(null);
                if (order != null) {
                    OrderStatus orderStatus = OrderStatus.valueOf(updateRequest.getStatus());
                    order.setStatus(orderStatus);
                    Order orderResult = orderRepository.save(order);
                    logService.logAction("UPDATE","ORDER", orderResult.getId(), token);
                    resp.setMessage("Order status updated successfully!");
                    resp.setStatusCode(200);
                    resp.setOrder(orderResult);
                } else {
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
