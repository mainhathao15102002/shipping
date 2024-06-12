package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.ReqRes;
import com.sb.shippingbackend.dto.UpdateBillStatusReq;
import com.sb.shippingbackend.entity.Bill;
import com.sb.shippingbackend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;

    public Bill findBillById(String billId) {
        return billRepository.findById(billId).orElse(null);
    }

    public List<Bill> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        return bills.stream()
                .sorted((b1, b2) -> b2.getCreatedDate().compareTo(b1.getCreatedDate()))
                .collect(Collectors.toList());
    }

//    public Bill findBillByShippingCode(String shippingCode) {
//        return billRepository.findByShippingCode(shippingCode);
//    }

    public ReqRes updateStatus(UpdateBillStatusReq updateReq) {
        ReqRes resp = new ReqRes();
        try {
            Bill bill = billRepository.findById(updateReq.getBillId()).orElse(null);

            if (bill!=null) {
                if (bill.getBillStatus() == 0) {
                    bill.setBillStatus((byte) 1);
                    billRepository.save(bill);
                    resp.setMessage("SUCCESSFUL!");
                    resp.setStatusCode(200);
                } else {
                    resp.setMessage("CANT UPDATE STATUS!");
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
