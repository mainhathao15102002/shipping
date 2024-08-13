package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.*;
import com.sb.shippingbackend.dto.response.CalculateCostRes;
import com.sb.shippingbackend.dto.response.DirectPaymentRes;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.*;
import com.sb.shippingbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private LogRepository logRepository;

    @Autowired
    private LogService logService;

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
    @Autowired
    private InternalShippingRepository internalShippingRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public static double roundToHalf(double d) {
        return Math.round(d * 2) / 2.0;
    }

    public CalculateCostRes calculateCost(CalculateCostReq calculateCostReq) {
        CalculateCostRes calculateCostRes = new CalculateCostRes();
        try {
            double totalSpecPropsCost = 0.0;
            if (!calculateCostReq.getSpecialProps().isEmpty()) {
                List<SpecialProps> specialPropList = specicalPropRepository.findSpecialPropsByIds(calculateCostReq.getSpecialProps());
                for (SpecialProps prop : specialPropList) {
                    totalSpecPropsCost += prop.getPostage();
                }
            }
            double totalWeight = calculateCostReq.getTotalWeight();
            if (!calculateCostReq.isIntraProvincial()) {
                double distance = calculateCostReq.getDistance();
                double estimatedDeliveryTime1 = calculateCostReq.getEstimatedDeliveryTime();
                double estimatedDeliveryTime = roundToHalf(estimatedDeliveryTime1 > 0 ? estimatedDeliveryTime1 / 24 : 0);
                double COST_PER_KM = 0.0;
                if (distance < 100) {
                    COST_PER_KM = PERCENT_0KM_100KM;
                    estimatedDeliveryTime += 1;
                } else if (distance >= 100 && distance < 500) {
                    COST_PER_KM = PERCENT_100KM_500KM;
                    estimatedDeliveryTime += 2;
                } else if (distance >= 500 && distance < 1000) {
                    COST_PER_KM = PERCENT_500KM_1000KM;
                    estimatedDeliveryTime += 4;
                } else {
                    COST_PER_KM = PERCENT_1000KM_HIGHER;
                    estimatedDeliveryTime += 6;
                }
                double COST_PER_KG = totalWeight < 1000 ? 0 : totalWeight / 1000 * BASE_COST_PER_KG;
                calculateCostRes.setCost(BASE_COST_KM + (BASE_COST_KM * COST_PER_KM) / 100 + COST_PER_KG + totalSpecPropsCost);
                calculateCostRes.setEstimatedDeliveryTime(estimatedDeliveryTime);
            } else {
                double estimatedDeliveryTime = 1;
                double cost = 0.0;
                if (totalWeight >= 4000) {
                    cost = COST_HIGHER_5000KG_INTRA_PROVINCIAL + totalWeight / 1000 - 4000 * COST_PER_KG_OVER_4KG + totalSpecPropsCost;
                } else {
                    cost = COST_HIGHER_5000KG_INTRA_PROVINCIAL + totalSpecPropsCost;
                }
                calculateCostRes.setCost(cost);
                calculateCostRes.setEstimatedDeliveryTime(estimatedDeliveryTime);
            }
            return calculateCostRes;
        } catch (Exception e) {
            calculateCostRes.setError(e.getMessage());
            return calculateCostRes;
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
                Optional<Order> orderOpt = orderRepository.findById(tmp.getOrderId());

                if (orderOpt.isPresent()) {
                    Order order = orderOpt.get();
                    LocalDateTime currentDateTime = LocalDateTime.now();

                    Bill bill = new Bill();
                    bill.setCreatedDate(currentDateTime);
                    bill.setBillStatus(true);
                    Bill billResult = billRepository.save(bill);

                    if (!billResult.getId().isEmpty()) {
                        TotalCostId totalCostId = new TotalCostId(tmp.getOrderId(), billResult.getId());
                        TotalCost totalCost = new TotalCost();
                        totalCost.setId(totalCostId);
                        totalCost.setTotalCost(tmp.getTotalCost());
                        totalCost.setOrder(order);
                        totalCost.setBill(bill);
                        totalCostRepository.save(totalCost);

                        order.setIsPaid(true);
                        orderRepository.save(order);

                        DateTimeFormatter formatterRes = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        resp.setBillId(billResult.getId());
                        resp.setCreatedDate(billResult.getCreatedDate().format(formatterRes));
                        resp.setTotalCost(totalCost.getTotalCost());
                        resp.setBillStatus(billResult.isBillStatus() ? "PAID" : "INACTIVE");
                        resp.setOrderId(billResult.getId());
                        resp.setMessage("COMPLETED PAYMENT!");
                        resp.setStatusCode(200);
                    }
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    resp.setMessage("ORDER NOT FOUND");
                    resp.setStatusCode(404);
                }
            } else {
                // Rollback transaction if temp bill is not found
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                resp.setMessage("TEMP BILL NOT FOUND");
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
            order.setReceiveAtPostOffice(createRequest.isReceiveAtPostOffice());
            LocalDate today = LocalDate.now();
            order.setEstimatedDeliveryDate(today.plusDays((long) createRequest.getEstimatedDeliveryDate()));
            order.setCustomer(customer);

            PostOffice postOffice = createRequest.getPostOfficeId() != null ? postOfficeRepository.findById(createRequest.getPostOfficeId()).orElseThrow(null) : null;
            if (postOffice == null) {
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
                merchandise.setOrder(orderResult);
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
            bill.setCreatedDate(currentDateTime);
            bill.setOrderId(order.getId());
            tmpBillRepository.save(bill);
            if (!orderResult.getId().isEmpty()) {
                resp.setOrder(orderResult);
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
    public ReqRes createOrders(List<CreateOrderReq> createRequests, String token) {
        ReqRes resp = new ReqRes();
        List<Order> createdOrders = new ArrayList<>();
        List<Temp_bill> tempBills = new ArrayList<>();
        try {
            for (CreateOrderReq createRequest : createRequests) {
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
                order.setReceiveAtPostOffice(createRequest.isReceiveAtPostOffice());
                LocalDate today = LocalDate.now();
                order.setEstimatedDeliveryDate(today.plusDays((long) createRequest.getEstimatedDeliveryDate()));
                order.setCustomer(customer);

                PostOffice postOffice = createRequest.getPostOfficeId() != null ? postOfficeRepository.findById(createRequest.getPostOfficeId()).orElseThrow(null) : null;
                if (postOffice == null) {
                    resp.setMessage("NOT FOUND POST OFFICE ID!");
                    resp.setStatusCode(200);
                    return resp;
                }
                order.setPostOffice(postOffice);

                Order orderResult = orderRepository.save(order);
                createRequest.getMerchandiseList().forEach((item) -> {
                    Merchandise merchandise = new Merchandise();
                    merchandise.setDesc(item.getDesc());
                    merchandise.setSize(item.getSize());
                    merchandise.setValue(item.getValue());
                    merchandise.setWeight(item.getWeight());
                    merchandise.setImageUrl(item.getImageUrl());
                    merchandise.setQuantity(item.getQuantity());
                    merchandise.setOrder(orderResult);
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
                bill.setCreatedDate(currentDateTime);
                bill.setOrderId(order.getId());
                tempBills.add(bill);
                createdOrders.add(orderResult);
            }
            tmpBillRepository.saveAll(tempBills);
            orderRepository.saveAll(createdOrders);
            resp.setMessage("Successful!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes createPayment(String vnp_TxnRef, String vnp_Amount, String vnp_PayDate ,String vnp_ResponseCode) {
        ReqRes resp = new ReqRes();
        try {
            Payment payment = new Payment();
            payment.setVnp_Amount(vnp_Amount);
            payment.setVnp_PayDate(vnp_PayDate);
            payment.setVnp_ResponseCode(vnp_ResponseCode);
            payment.setVnp_TxnRef(vnp_TxnRef);
            paymentRepository.save(payment);
            resp.setStatusCode(200);
            resp.setError("SUCCESSFULLY!");

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes updatePaid(String orderId, String token) {
        ReqRes resp = new ReqRes();
        try {
            Order order = orderRepository.findById(orderId).orElseThrow(null);
            if (order != null) {
                order.setIsPaid(true);
                Order orderResult = orderRepository.save(order);
                resp.setStatusCode(200);
                resp.setOrder(orderResult);
            } else {
                resp.setMessage("NOT FOUND!");
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
                    if (orderStatus == OrderStatus.CONFIRMED) {
                        LocalDate today = LocalDate.now();
                        long daysBetween = ChronoUnit.DAYS.between(order.getCreatedDate(), order.getEstimatedDeliveryDate());
                        order.setEstimatedDeliveryDate(today.plusDays(daysBetween));
                    }
                    order.setStatus(orderStatus);
                    Order orderResult = orderRepository.save(order);
                    logService.logAction("UPDATE", "ORDER", orderResult.getId(), token);
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

    public ReqRes updateCostVariables(UpdateCostReq updateCostReq) {
        ReqRes resp = new ReqRes();
        try {
            this.BASE_COST_KM = updateCostReq.getBaseCostKm() == 0.0 ? this.BASE_COST_KM : updateCostReq.getBaseCostKm();
            this.PERCENT_0KM_100KM = updateCostReq.getPercent0km100km() == 0.0 ? this.PERCENT_0KM_100KM : updateCostReq.getPercent0km100km();
            this.PERCENT_100KM_500KM = updateCostReq.getPercent100km500km() == 0.0 ? this.PERCENT_100KM_500KM : updateCostReq.getPercent100km500km();
            this.PERCENT_500KM_1000KM = updateCostReq.getPercent500km1000km() == 0.0 ? this.PERCENT_500KM_1000KM : updateCostReq.getPercent500km1000km();
            this.PERCENT_1000KM_HIGHER = updateCostReq.getPercent1000kmHigher() == 0.0 ? this.PERCENT_1000KM_HIGHER : updateCostReq.getPercent1000kmHigher();
            this.BASE_COST_PER_KG = updateCostReq.getBaseCostPerKg() == 0.0 ? this.BASE_COST_PER_KG : updateCostReq.getBaseCostPerKg();
            this.COST_PER_KG_OVER_4KG = updateCostReq.getCostPerKgOver4kg() == 0.0 ? this.COST_PER_KG_OVER_4KG : updateCostReq.getCostPerKgOver4kg();
            this.COST_HIGHER_5000KG_INTRA_PROVINCIAL = updateCostReq.getCostHigher5000kgIntraProvincial() == 0.0 ? this.COST_HIGHER_5000KG_INTRA_PROVINCIAL : updateCostReq.getCostHigher5000kgIntraProvincial();
            resp.setMessage("Cost variables updated successfully!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes getOrdersForPostOffices(String internalShippingId, String token) {
        ReqRes resp = new ReqRes();
        try {

            InternalShipping internalShipping = internalShippingRepository.findById(internalShippingId)
                    .orElseThrow(() -> new RuntimeException("InternalShipping not found"));
            String username = jwtUtils.extractUsername(token);
            PostOffice postOffice = postOfficeRepository.findByUsername(username);

            String listPostOffice = internalShipping.getListPostOffice();
            String listPostOfficeCompleted = internalShipping.getListPostOfficeCompleted();

            List<Integer> postOfficeIds = Arrays.stream(listPostOffice.split("-"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            List<Integer> postOfficeIdsCompleted = new ArrayList<>((listPostOfficeCompleted != null && !listPostOfficeCompleted.isEmpty()) ?
                    Arrays.stream(listPostOfficeCompleted.split("-"))
                            .map(Integer::parseInt)
                            .toList() : Collections.emptyList());

            postOfficeIdsCompleted.add(postOffice.getId());

            postOfficeIds.removeAll(postOfficeIdsCompleted);

            List<Order> orders = orderRepository.findByPostOfficeIdInAndStatus(postOfficeIds, OrderStatus.CONFIRMED);
            if (orders != null && !orders.isEmpty()) {
                resp.setOrderList(orders);
                resp.setMessage("Orders found successfully!");
                resp.setStatusCode(200);
            } else {
                resp.setMessage("No orders found!");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    @Transactional
    public ReqRes cancelOrderWhenErrorPayOnline(String orderId) {
        ReqRes resp = new ReqRes();
        try {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
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

    @Transactional
    public DirectPaymentRes paymentOnline(String orderId, String token) {
        DirectPaymentRes resp = new DirectPaymentRes();
        try {
            Payment payment = paymentRepository.findById(orderId).orElseThrow(null);
            if (payment.getVnp_ResponseCode().equals("00")) {
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime dateTime = LocalDateTime.parse(payment.getVnp_PayDate(), formatter1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Bill bill = new Bill();
                bill.setCreatedDate(dateTime);
                bill.setBillStatus(true);
                Bill billResult = billRepository.save(bill);
                if (!billResult.getId().isEmpty()) {
                    TotalCostId totalCostId = new TotalCostId(payment.getVnp_TxnRef(), billResult.getId());
                    TotalCost totalCost = new TotalCost();
                    totalCost.setId(totalCostId);
                    totalCost.setTotalCost(Double.parseDouble(payment.getVnp_Amount()) / 100);
                    Optional<Order> order = orderRepository.findById(payment.getVnp_TxnRef());
                    order.ifPresent(totalCost::setOrder);
                    totalCost.setBill(bill);
                    totalCostRepository.save(totalCost);
                    resp.setBillId(billResult.getId());
                    resp.setCreatedDate(billResult.getCreatedDate().format(formatter));
                    resp.setTotalCost(totalCost.getTotalCost());
                    resp.setBillStatus(billResult.isBillStatus() ? "PAID" : "INACTIVE");
                    resp.setOrderId(billResult.getId());
                    resp.setMessage("COMPLETED PAYMENT!");
                    resp.setStatusCode(200);

                }
            } else {

                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                resp.setMessage("FAILED PAYMENT!");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
