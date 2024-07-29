package com.sascom.chickenstock.domain.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public abstract class TradeRequest implements Comparable<TradeRequest> {
    private final Long accountId;
    private final Long memberId;
    private final Long companyId;
    private final Long competitionId;
    private final String companyName;
    private final Integer unitCost;
    @Setter
    private Integer amount;
    private final LocalDateTime orderTime;
}