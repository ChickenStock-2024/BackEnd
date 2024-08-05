package com.sascom.chickenstock.domain.auth.dto.token;

import lombok.Builder;

import java.time.LocalDateTime;

public record TokenDto (
    String accessToken,
    String refreshToken
){
}