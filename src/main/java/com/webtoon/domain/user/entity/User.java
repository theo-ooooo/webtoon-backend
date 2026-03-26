package com.webtoon.domain.user.entity;

import com.webtoon.global.enums.Role;
import com.webtoon.global.exception.ApiException;
import com.webtoon.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false)
    private Integer coinBalance = 0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void promoteToAdmin() {
        this.role = Role.ADMIN;
    }

    public void chargeCoin(int amount) {
        this.coinBalance += amount;
    }

    public void useCoin(int amount) {
        if (this.coinBalance < amount) {
            throw new ApiException(ErrorCode.INSUFFICIENT_COIN);
        }
        this.coinBalance -= amount;
    }
}
