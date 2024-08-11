package com.sascom.chickenstock.domain.fcmtoken.dto.request;

public record FcmTokenRequest (
        Long memberId,
        String fcmToken
) {
}
