package com.sascom.chickenstock.domain.fcmtoken.dto.response;

import lombok.Builder;

@Builder
public record FcmTokenResponse (
        Long memberId,
        String fcmToken,
        String message
) {
}
