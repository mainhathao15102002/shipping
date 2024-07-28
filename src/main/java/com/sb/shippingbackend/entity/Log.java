package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "log")
@EqualsAndHashCode
@ToString(exclude = {"user"})
public class Log {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "mataikhoan", nullable = false)
    @JsonIgnoreProperties({"id", "password","logs","role","tokenList","authorities","enabled","username","accountNonExpired","credentialsNonExpired","accountNonLocked"})
    private User user;


    @Column(name = "thaotac")
    private String action;

    @Column(name = "ngaygiothuchien", nullable = false, updatable = false)
    private LocalDateTime actionDateTime;

    @Column(name = "bang")
    private String table;

    @Column(name = "ma")
    private String idObject;

    @PrePersist
    protected void onCreate() {
        actionDateTime = LocalDateTime.now();
    }
}
