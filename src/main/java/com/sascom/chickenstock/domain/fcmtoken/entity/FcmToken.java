package com.sascom.chickenstock.domain.fcmtoken.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor
public class FcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private String token;

    public FcmToken(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }

    public void updateToken(String token) {
        if (!StringUtils.hasText(token)) {
            throw new IllegalArgumentException("fcm token이 없습니다.");
        }
        if (token.equals(this.token)) {
            return;
        }
        this.token = token;
    }
}
