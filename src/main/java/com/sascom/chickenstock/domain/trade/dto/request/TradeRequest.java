package com.sascom.chickenstock.domain.trade.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public abstract class TradeRequest {
    private Long accountId;
    private Long memberId;
    private Long companyId;
    private Long competitionId;
    private String companyName;
    private Integer unitCost;
    @Setter
    private Integer amount;
    private LocalDateTime orderTime;
}