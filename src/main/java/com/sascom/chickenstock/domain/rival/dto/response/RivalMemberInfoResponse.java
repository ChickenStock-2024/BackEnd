package com.sascom.chickenstock.domain.rival.dto.response;

import lombok.Builder;

@Builder
public record RivalMemberInfoResponse(
        Long id,
        String nickname
) {
}
