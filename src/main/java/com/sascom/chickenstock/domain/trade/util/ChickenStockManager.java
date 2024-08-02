package com.sascom.chickenstock.domain.trade.util;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.sascom.chickenstock.domain.trade.dto.MatchStatus;
import com.sascom.chickenstock.domain.trade.dto.ProcessedOrderDto;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;

import java.util.List;

public class ChickenStockManager implements StockManager {
    private ChickenStockQueue<SellTradeRequest> sellQueue;
    private ChickenStockQueue<BuyTradeRequest> buyQueue;
    public ChickenStockManager() {
        sellQueue = new ChickenStockQueueImpl<>();
        buyQueue = new ChickenStockQueueImpl<>();
    }

    @Override
    public void match(int marketPrice, List<ProcessedOrderDto> canceled, List<ProcessedOrderDto> executed) {
        SellTradeRequest sellTradeRequest = null;
        BuyTradeRequest buyTradeRequest = null;
        while(true) {
            if(sellTradeRequest == null) {
                addCanceledTradeRequestToList(
                        sellQueue.remove(marketPrice),
                        canceled,
                        MatchStatus.CANCELED_BY_LOGIC);
                sellTradeRequest = sellQueue.first(marketPrice);
            }
            if(buyTradeRequest == null) {
                addCanceledTradeRequestToList(
                        buyQueue.remove(marketPrice),
                        canceled,
                        MatchStatus.CANCELED_BY_LOGIC
                );
                buyTradeRequest = buyQueue.first(marketPrice);
            }
            if(sellTradeRequest == null || buyTradeRequest == null) {
                break;
            }
            int executionVolume = Math.min(sellTradeRequest.getRemainingVolume(), buyTradeRequest.getRemainingVolume());
            // TODO: validate balance.

            // if 둘 중 하나라도 가격검증 실패 -> continue;
            sellTradeRequest.addExecutedVolume(executionVolume);
            executed.add(tradeRequestToProcessedOrderDto(sellTradeRequest, MatchStatus.EXECUTED));
            buyTradeRequest.addExecutedVolume(executionVolume);
            executed.add(tradeRequestToProcessedOrderDto(buyTradeRequest, MatchStatus.EXECUTED));

            if(sellTradeRequest.getTotalOrderVolume().equals(sellTradeRequest.getExecutedVolume())) {
                sellQueue.remove(sellTradeRequest);
                sellTradeRequest = null;
            }
            if(buyTradeRequest.getTotalOrderVolume().equals(buyTradeRequest.getExecutedVolume())) {
                buyQueue.remove(buyTradeRequest);
                buyTradeRequest = null;
            }
        }
        return;
    }

    @Override
    public boolean order(SellTradeRequest tradeRequest) {
        return sellQueue.add(tradeRequest) != null;
    }

    @Override
    public boolean order(BuyTradeRequest tradeRequest) {
        return buyQueue.add(tradeRequest) != null;
    }

    @Override
    public boolean cancel(SellTradeRequest tradeRequest) {
        return sellQueue.add(tradeRequest) != null;
    }

    @Override
    public boolean cancel(BuyTradeRequest tradeRequest) {
        return buyQueue.add(tradeRequest) != null;
    }

    private void addCanceledTradeRequestToList(
            List<? extends TradeRequest> tradeRequests,
            List<ProcessedOrderDto> list,
            MatchStatus matchStatus
    ) {
        for(TradeRequest tradeRequest : tradeRequests) {
            list.add(canceledTradeRequestToProcessedOrderDto(tradeRequest, matchStatus));
        }
        return;
    }

    private void addCanceledTradeRequestToList(
            TradeRequest tradeRequest,
            List<ProcessedOrderDto> list,
            MatchStatus matchStatus
    ) {
        list.add(canceledTradeRequestToProcessedOrderDto(tradeRequest, matchStatus));
    }

    private ProcessedOrderDto canceledTradeRequestToProcessedOrderDto(
            TradeRequest tradeRequest,
            MatchStatus matchStatus) {
        return ProcessedOrderDto.builder()
                .accountId(tradeRequest.getAccountId())
                .requestHistoryId(tradeRequest.getHistoryId())
                .companyName(tradeRequest.getCompanyName())
                .price(tradeRequest.getUnitCost())
                .volume(tradeRequest.getRemainingVolume())
                .orderType(tradeRequest.getOrderType())
                .matchStatus(matchStatus)
                .build();
    }

    private ProcessedOrderDto executedTradeRequestToProcessedOrderDto(
            TradeRequest tradeRequest
    )
}
