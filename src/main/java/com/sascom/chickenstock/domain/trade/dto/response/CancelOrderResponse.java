package com.sascom.chickenstock.domain.trade.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CancelOrderResponse(
        Long accountId,
        Long memberId,
        Long companyId,
        Long competitionId,
        String companyName,
        Integer totalOrderVolume,
        Integer executedVolume,
        Integer cancelVolume,
        LocalDateTime cancelTime) {
}
