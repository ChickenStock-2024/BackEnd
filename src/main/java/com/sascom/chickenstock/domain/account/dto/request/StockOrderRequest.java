package com.sascom.chickenstock.domain.account.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;

import java.time.LocalDateTime;

public record StockOrderRequest (
        Long accountId,
        Long memberId,
        Long companyId,
        Long competitionId,
        String companyName,
        Integer unitCost,
        Integer amount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime orderTime
) {

        public BuyTradeRequest toBuyTradeRequestEntity() {
                return BuyTradeRequest.builder()
                        .accountId(accountId)
                        .memberId(memberId)
                        .companyId(companyId)
                        .companyName(companyName)
                        .competitionId(competitionId)
                        .unitCost(unitCost)
                        .totalOrderVolume(amount)
                        .orderTime(orderTime)
                        .build();
        }


        public SellTradeRequest toSellTradeRequestEntity() {
                return SellTradeRequest.builder()
                        .accountId(accountId)
                        .memberId(memberId)
                        .companyId(companyId)
                        .companyName(companyName)
                        .competitionId(competitionId)
                        .unitCost(unitCost)
                        .totalOrderVolume(amount)
                        .orderTime(orderTime)
                        .build();
        }

}
