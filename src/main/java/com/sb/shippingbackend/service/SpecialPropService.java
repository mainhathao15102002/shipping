package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.Address;
import com.sb.shippingbackend.entity.SpecialProps;
import com.sb.shippingbackend.repository.SpecicalPropRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpecialPropService {
    @Autowired
    private SpecicalPropRepository specicalPropRepository;

    public ReqRes getAll() {
        ReqRes resp = new ReqRes();
        try {
            List<SpecialProps> specialProps = specicalPropRepository.findAllSpecialProps();
            if (specialProps.isEmpty()) {
                resp.setMessage("Not value");
                resp.setStatusCode(404);
            } else {
                resp.setSpecialPropsList(specialProps);
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

}
