package com.sb.shippingbackend.controller;

import com.sb.shippingbackend.dto.request.CreateOrderReq;
import com.sb.shippingbackend.dto.response.ReqRes;
import com.sb.shippingbackend.repository.PostOfficeRepository;
import com.sb.shippingbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adminuser/postOffice")
public class PostOfficeController {
    @Autowired
    private PostService postService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(postService.getAllOfficeInfo());
    }
}
