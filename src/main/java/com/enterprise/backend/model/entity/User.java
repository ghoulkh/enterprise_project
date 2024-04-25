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
@Table(indexes = {@Index(name = "userEmailIndex", columnList = "email"), @Index(name = "userPhoneIndex", columnList = "phone")})
public class User extends Auditable implements Serializable {
    @Id
    private String id;

    private String password;
    private String fullName;
    private String avaUrl;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String address;
    private boolean isActive = true;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Set<Review> reviews;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Set<Authority> authorities;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private Set<CodeForgotPass> codes;

    public enum Provider {
        LOCAL, GOOGLE, FACEBOOK
    }
}
