package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.response.PostOfficeRes;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.PostOffice;
import com.sb.shippingbackend.repository.PostOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostOfficeRepository postOfficeRepository;

    public PostOfficeRes getAllOfficeInfo()
    {
        PostOfficeRes resp = new PostOfficeRes();
        try {
            List<PostOffice> postOfficeList = postOfficeRepository.findAll();
            if (!postOfficeList.isEmpty()) {
                resp.setPostOfficeList(postOfficeList);
                resp.setStatusCode(200);
            } else {
                resp.setMessage("NOT DATA");
                resp.setStatusCode(404);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }
}
