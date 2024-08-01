package com.sascom.chickenstock.global.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class JwtProvider {
    public LocalDateTime getAccessTokenExpirationDate() {
        return null;
    }

    public LocalDateTime getRefreshTokenExpirationDate() {
        return null;
    }

    public String createToken(Authentication authentication, LocalDateTime accessTokenExpirationDate) {
        return null;
    }
}
