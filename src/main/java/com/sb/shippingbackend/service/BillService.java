package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.response.BillResponse;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.dto.request.UpdateBillStatusReq;
import com.sb.shippingbackend.entity.Bill;
import com.sb.shippingbackend.entity.TotalCost;
import com.sb.shippingbackend.repository.BillRepository;
import com.sb.shippingbackend.repository.OrderRepository;
import com.sb.shippingbackend.repository.TotalCostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private TotalCostRepository totalCostRepository;

    @Autowired
    private OrderRepository orderRepository;

    public BillResponse findBillById(String billId) {
        BillResponse resp = new BillResponse();
        Bill bill = billRepository.findById(billId).orElse(null);
        if (bill != null)
        {
            resp.setBillId(bill.getId());
            resp.setOrderId(bill.getTotalCost().getId().getOrderId());
            resp.setCreatedDate(bill.getCreatedDate());
            resp.setTotalCost(bill.getTotalCost().getTotalCost());
            resp.setBillStatus(bill.isBillStatus()?"PAID":"INACTIVE");
        }
        return resp;
    }
    public List<BillResponse> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .sorted((b1, b2) -> b2.getCreatedDate().compareTo(b1.getCreatedDate())) // Sort by createdDate descending
                .map(bill -> {
                    BillResponse resp = new BillResponse();
                    resp.setBillId(bill.getId());
                    resp.setTotalCost(bill.getTotalCost().getTotalCost());
                    resp.setOrderId(bill.getTotalCost().getId().getOrderId());
                    resp.setCreatedDate(bill.getCreatedDate());
                    resp.setBillStatus(bill.isBillStatus() ? "PAID" : "INACTIVE");
                    return resp;
                }).collect(Collectors.toList());
    }



    public List<BillResponse> getBillsByCustomerId(String customerId) {
        List<Bill> bills = billRepository.findAllByCustomerId(customerId);
        return bills.stream().map(bill -> {
            BillResponse resp = new BillResponse();
            resp.setBillId(bill.getId());
            resp.setOrderId(bill.getTotalCost().getId().getOrderId());
            resp.setCreatedDate(bill.getCreatedDate());
            resp.setBillStatus(bill.isBillStatus()?"PAID":"INACTIVE");
            return resp;
        }).collect(Collectors.toList());
    }

    public BillResponse findBillByOrder_Id(String orderId) {
        BillResponse resp = new BillResponse();
        Bill bill = billRepository.findBillByOrder_Id(orderId);
        resp.setBillId(bill.getId());
        resp.setOrderId(bill.getTotalCost().getId().getOrderId());
        resp.setCreatedDate(bill.getCreatedDate());
        resp.setBillStatus(bill.isBillStatus()?"PAID":"INACTIVE");
        return resp;
    }

    public ReqRes updateStatus(UpdateBillStatusReq updateReq) {
        ReqRes resp = new ReqRes();
        try {
            Bill bill = billRepository.findById(updateReq.getBillId()).orElse(null);

            if (bill!=null) {
                if (bill.isBillStatus()) {
                    bill.setBillStatus(false);
                    billRepository.save(bill);
                    resp.setMessage("Deactivated!");
                    resp.setStatusCode(200);
                } else {
                    resp.setMessage("Cant Deactivated this bill!");
                    resp.setStatusCode(200);
                }
            }
            else {
                resp.setMessage("NOT FOUND!");
                resp.setStatusCode(404);
            }

        }
        catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
            throw e;
        }
        return resp;
    }
}
