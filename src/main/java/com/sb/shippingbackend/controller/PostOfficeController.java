package com.sb.shippingbackend.controller;


import com.sb.shippingbackend.dto.request.PostOfficeReq;
import com.sb.shippingbackend.service.PostService;
import com.sb.shippingbackend.service.Utils;
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
    @GetMapping("/get-by-user")
    public ResponseEntity<?> create( @RequestHeader("Authorization") String token) {
        final String jwtToken = Utils.getToken(token);
        if(jwtToken == null) {
            return ResponseEntity.status(500).body("token is not valid");
        }
        return ResponseEntity.ok(postService.getPostOfficeByToken(jwtToken));
    }


    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody PostOfficeReq postOfficeReq) {
        return ResponseEntity.ok(postService.updatePostOffice(postOfficeReq));
    }
}
