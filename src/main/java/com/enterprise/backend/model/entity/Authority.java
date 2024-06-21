package com.enterprise.backend.model.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Authority implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Getter
    public enum Role {
        ROLE_ADMIN("ROLE_ADMIN"),
        ROLE_USER("ROLE_USER"),
        ROLE_SUPER_ADMIN("ROLE_SUPER_ADMIN");
        private final String value;
        Role(String value) {
            this.value = value;
        }
    }
}

