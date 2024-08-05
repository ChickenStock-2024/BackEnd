package com.sascom.chickenstock.domain.auth.dto.token;

import lombok.Builder;

import java.time.LocalDateTime;

public record TokenDto (
    String id,
    Integer count,
    String grantType,
    String accessToken,
    String refreshToken,
    LocalDateTime accessTokenExpiresIn
){

    @Builder
    public TokenDto(String id, Integer count, String grantType, String accessToken, String refreshToken, LocalDateTime accessTokenExpiresIn) {
        this.id = id;
        this.count = count;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }
}