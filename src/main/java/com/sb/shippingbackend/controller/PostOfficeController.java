package com.sb.shippingbackend.controller;


import com.sb.shippingbackend.dto.request.PostOfficeReq;
import com.sb.shippingbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v3/postOffice")
public class PostOfficeController {
    @Autowired
    private PostService postService;

    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(postService.getAllOfficeInfo());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody PostOfficeReq postOfficeReq) {
        return ResponseEntity.ok(postService.createPostOffice(postOfficeReq));
    }



    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PostOfficeReq postOfficeReq) {
        return ResponseEntity.ok(postService.updatePostOffice(postOfficeReq));
    }
}
