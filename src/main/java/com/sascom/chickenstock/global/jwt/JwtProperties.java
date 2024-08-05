package com.sascom.chickenstock.global.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
@ConfigurationProperties("jwt")
public record JwtProperties (
        TokenProperties accessToken,
        TokenProperties refreshToken,
        String secret,
        String bearerType
) {
    public  record TokenProperties(
            Duration expireDuration, String cookieName) {
    }
}