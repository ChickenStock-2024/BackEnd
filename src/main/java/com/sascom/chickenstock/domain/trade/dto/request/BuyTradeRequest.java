package com.sascom.chickenstock.domain.trade.dto.request;

import com.sascom.chickenstock.domain.trade.dto.OrderType;
import lombok.*;

import java.time.LocalDateTime;

public class BuyTradeRequest extends TradeRequest implements Comparable<BuyTradeRequest> {

    @Builder
    public BuyTradeRequest(OrderType OrderType, Long accountId, Long memberId, Long companyId, Long competitionId, String companyName, Integer unitCost, Integer amount, LocalDateTime orderTime) {
        super(OrderType, accountId, memberId, companyId, competitionId, companyName, unitCost, amount, orderTime);
    }

    public int compareByUnitCost(BuyTradeRequest other) {
        return this.getUnitCost().compareTo(other.getUnitCost());
    }

    @Override
    public int compareTo(BuyTradeRequest other) {
//        // 가격 우선의 법칙
//        if (!getUnitCost().equals(other.getUnitCost())) {
//            return this.getUnitCost().compareTo(other.getUnitCost());
//        }
//        // 시간 우선의 법칙
//        if (!getOrderTime().isEqual(other.getOrderTime())) {
//            return this.getOrderTime().compareTo(other.getOrderTime());
//        }
//        // 수량 우선의 법칙
//        return this.getAmount().compareTo(other.getAmount());
        if (compareByUnitCost(other) != 0) {
            return compareByUnitCost(other);
        }
        return compareByOrderTimeAndAmount(other);
    }
}
