package com.sascom.chickenstock.domain.account.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sascom.chickenstock.domain.trade.dto.OrderType;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record StockOrderRequest (
        Long accountId,
        Long memberId,
        Long companyId,
        Long competitionId,
        String companyName,
        Integer unitCost,
        Integer volume
) {
        public BuyTradeRequest toBuyTradeRequestEntity(
                Long historyId,
                LocalDateTime orderTime,
                OrderType orderType) {
                return BuyTradeRequest.builder()
                        .orderType(orderType)
                        .accountId(accountId)
                        .memberId(memberId)
                        .companyId(companyId)
                        .historyId(historyId)
                        .companyName(companyName)
                        .competitionId(competitionId)
                        .unitCost(orderType == OrderType.LIMIT? unitCost : 0)
                        .totalOrderVolume(volume)
                        .orderTime(orderTime)
                        .build();
        }

        public SellTradeRequest toSellTradeRequestEntity(
                Long historyId,
                LocalDateTime orderTime,
                OrderType orderType) {
                return SellTradeRequest.builder()
                        .orderType(orderType)
                        .accountId(accountId)
                        .memberId(memberId)
                        .companyId(companyId)
                        .historyId(historyId)
                        .companyName(companyName)
                        .competitionId(competitionId)
                        .unitCost(orderType == OrderType.LIMIT? unitCost : 0)
                        .totalOrderVolume(volume)
                        .orderTime(orderTime)
                        .build();
        }

}
