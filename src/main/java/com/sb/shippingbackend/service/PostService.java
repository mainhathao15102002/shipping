package com.sb.shippingbackend.service;

import com.sb.shippingbackend.dto.request.PostOfficeReq;
import com.sb.shippingbackend.dto.response.PostOfficeRes;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.entity.Employee;
import com.sb.shippingbackend.entity.PostOffice;
import com.sb.shippingbackend.repository.EmployeeRepository;
import com.sb.shippingbackend.repository.PostOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostOfficeRepository postOfficeRepository;

    @Autowired
    private JWTUtils jwtUtil;
    @Autowired
    private EmployeeRepository employeeRepository;

    public PostOfficeRes getAllOfficeInfo() {
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

    public ReqRes updatePostOffice(PostOfficeReq postOfficeReq) {
        ReqRes resp = new ReqRes();
        try {
            PostOffice postOffice = postOfficeRepository.findById(postOfficeReq.getId()).orElseThrow(null);
            if (postOffice != null) {
                if(postOffice.getName()!=null)
                {
                    postOffice.setName(postOfficeReq.getName());

                }
                if(postOfficeReq.getAddress()!=null)
                {
                    postOffice.setAddress(postOfficeReq.getAddress());
                }
                if(postOfficeReq.getPhoneNumber()!=null)
                {
                    postOffice.setPhoneNumber(postOfficeReq.getPhoneNumber());

                }
                postOfficeRepository.save(postOffice);
                resp.setMessage("SUCCESSFUL!");
                resp.setStatusCode(200);
            }
            else {
                resp.setMessage("NOT FOUND!");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;

    }

    public ReqRes createPostOffice(PostOfficeReq postOfficeReq) {
        ReqRes resp = new ReqRes();
        try {
            PostOffice postOffice = new PostOffice();
            postOffice.setAddress(postOfficeReq.getAddress());
            postOffice.setName(postOfficeReq.getName());
            postOffice.setStatus(postOfficeReq.getStatus());
            postOffice.setPhoneNumber(postOfficeReq.getPhoneNumber());
            postOfficeRepository.save(postOffice);
            resp.setMessage("SUCCESSFUL!");
            resp.setStatusCode(200);
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;

    }
    public PostOfficeRes getPostOfficeByToken(String token) {
        PostOfficeRes resp = new PostOfficeRes();
        try {
            String username = jwtUtil.extractUsername(token);
            Employee employee = employeeRepository.findByUserEmail(username);
            if (employee != null && employee.getPostOffice() != null) {
                PostOffice postOffice = employee.getPostOffice();
                resp.setPostOffice(postOffice);
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
