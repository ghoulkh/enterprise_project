package com.enterprise.backend.model.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
public class ProductOrder extends AuditableAware implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Orders> orders;

    private Integer quantity;
    private String receiverFullName;
    private String email;
    private String phoneNumber;
    private String addressDetail;
    private String note;
}
