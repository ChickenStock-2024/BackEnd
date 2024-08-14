package com.sascom.chickenstock.domain.account.dto.request;

import lombok.Getter;

public record CancelOrderRequest(
        Long accountId,
        Long memberId,
        Long competitionId,
        Long historyId
) {
}
