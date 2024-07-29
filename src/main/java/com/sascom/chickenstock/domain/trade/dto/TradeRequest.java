package com.sascom.chickenstock.domain.trade.dto;

import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

//@Getter
//@RequiredArgsConstructor
//public class TradeRequest implements Comparable<BuyTradeRequest> {
//    private final String type; // "buy" or "sell"
//    private final Long accountId;
//    private final Long memberId;
//    private final Long companyId;
//    private final Long competitionId;
//    private final Integer unitCost;
//    private final Integer amount;
//    private final LocalDateTime orderTime;
//
//    @Override
//    public int compareTo(BuyTradeRequest other) {
//
//        // 1. 가격 우선의 법칙
//        if(!unitCost.equals(other.getAmount())) {
//            return unitCost.compareTo(other.getAmount());
//        }
//        // 2. 시간 우선의 법칙
//        if(!orderTime.isEqual(other.getOrderTime())) {
//            return orderTime.compareTo(other.getOrderTime());
//        }
//        // 3. 수량 우선의 법칙
//        return this.amount.compareTo(other.getAmount());
//    }
//}