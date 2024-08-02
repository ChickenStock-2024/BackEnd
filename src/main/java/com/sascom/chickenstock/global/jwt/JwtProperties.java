package com.sascom.chickenstock.global.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@ConfigurationProperties("jwt")
public class JwtProperties {


    private final TokenProperties accessToken;
    private final TokenProperties refreshToken;
    private final String secret;

    public JwtProperties(
            TokenProperties accessToken, TokenProperties refreshToken,
            String secret) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.secret = secret;
    }

    public record TokenProperties(
            Duration expireDuration, String cookieName) {
    }
}
