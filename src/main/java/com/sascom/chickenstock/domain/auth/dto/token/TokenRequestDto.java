package com.sascom.chickenstock.domain.auth.dto.token;

public record TokenRequestDto (
        String accessToken,
        String refreshToken
) {
}