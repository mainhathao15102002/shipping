package com.sb.shippingbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "token")
public class Token {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "token")
    private String token;

    @Column(name="is_logged_out")
    private boolean loggedOut;

    @ManyToOne
    @JoinColumn(name = "mataikhoan")
    private User user;

}
