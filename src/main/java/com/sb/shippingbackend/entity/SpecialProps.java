package com.sb.shippingbackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "bangtinhchatdacbiet")
public class SpecialProps {
    @Id
    @Column(name = "matinhchat")
    private Integer id;

    @Column(name = "tentinhchat")
    private String propName;

    @OneToMany(mappedBy = "specialProps",fetch = FetchType.LAZY)
    @JsonIgnore
    private List<ListSpecicalPropOfMerchandise> list = new ArrayList<>();


}
