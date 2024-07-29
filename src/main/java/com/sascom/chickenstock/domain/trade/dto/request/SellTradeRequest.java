package com.sascom.chickenstock.domain.trade.dto.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class SellTradeRequest implements Comparable<SellTradeRequest>{
    private final Long accountId;
    private final Long memberId;
    private final Long companyId;
    private final Long competitionId;
    private final String companyName;
    private final Integer unitCost;
    @Setter private Integer amount;
    private final LocalDateTime orderTime;

    @Override
    public int compareTo(SellTradeRequest other) {

        // 1. 가격 우선의 법칙
        if(!unitCost.equals(other.getAmount())) {
            return unitCost.compareTo(other.getAmount());
        }
        // 2. 시간 우선의 법칙
        if(!orderTime.isEqual(other.getOrderTime())) {
            return orderTime.compareTo(other.getOrderTime());
        }
        // 3. 수량 우선의 법칙
        return this.amount.compareTo(other.getAmount());
    }
}