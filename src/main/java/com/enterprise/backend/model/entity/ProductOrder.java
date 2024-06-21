package com.enterprise.backend.model.entity;

import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.enums.OrderTypeStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductOrder extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "productOrder", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Orders> orders;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer quantity;
    private Long price;
    private String receiverFullName;
    private String email;
    private String phoneNumber;
    private String addressDetail;
    private String note;

    @Lob
    private String htmlContent;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @Enumerated(EnumType.STRING)
    private OrderTypeStatus type;
}
