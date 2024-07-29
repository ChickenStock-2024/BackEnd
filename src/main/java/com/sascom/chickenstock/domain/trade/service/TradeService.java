package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

@Service
public class TradeService {

    private final Map<String, PriorityBlockingQueue<BuyTradeRequest>> limitBuyOrderQueues;
    private final Map<String, PriorityBlockingQueue<SellTradeRequest>> limitSellOrderQueues;
    private final Map<String, PriorityBlockingQueue<BuyTradeRequest>> marketBuyOrderQueues;
    private final Map<String, PriorityBlockingQueue<SellTradeRequest>> marketSellOrderQueues;

    @Autowired
    public TradeService(Map<String, PriorityBlockingQueue<BuyTradeRequest>> limitBuyOrderQueues,
                        Map<String, PriorityBlockingQueue<SellTradeRequest>> limitSellOrderQueues,
                        Map<String, PriorityBlockingQueue<BuyTradeRequest>> marketBuyOrderQueues,
                        Map<String, PriorityBlockingQueue<SellTradeRequest>> marketSellOrderQueues) {
        this.limitBuyOrderQueues = limitBuyOrderQueues;
        this.limitSellOrderQueues = limitSellOrderQueues;
        this.marketBuyOrderQueues = marketBuyOrderQueues;
        this.marketSellOrderQueues = marketSellOrderQueues;
    }

    public TradeResponse addBuyRequest(BuyTradeRequest buyTradeRequest) {
        if(!limitBuyOrderQueues.containsKey(buyTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        limitBuyOrderQueues.get(buyTradeRequest.getCompanyName()).offer(buyTradeRequest);
        return matchBuyTrades(buyTradeRequest);
    }

    public TradeResponse addSellRequest(SellTradeRequest sellTradeRequest) {
        if(!limitSellOrderQueues.containsKey(sellTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        limitSellOrderQueues.get(sellTradeRequest.getCompanyName()).offer(sellTradeRequest);
        return matchSellTrades(sellTradeRequest);
    }

    public BuyTradeRequest processBuyRequest(String company) {
        return limitBuyOrderQueues.get(company).poll();
    }

    public SellTradeRequest processSellRequest(String company) {
        return limitSellOrderQueues.get(company).poll();
    }

    public boolean isBuyQueueEmpty(String company) {
        return limitBuyOrderQueues.get(company).isEmpty();
    }

    public boolean isSellQueueEmpty(String company) {
        return limitSellOrderQueues.get(company).isEmpty();
    }

    public TradeResponse matchBuyTrades(BuyTradeRequest buyTradeRequest) {
        String company = buyTradeRequest.getCompanyName();
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = limitBuyOrderQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = limitSellOrderQueues.get(company);

        // 우선순위 큐의 첫번째 요소를 가져오지만 제거하지 않음
        BuyTradeRequest buyRequest = buyQueue.peek();
        SellTradeRequest sellRequest = sellQueue.peek();

        if (buyRequest == null || sellRequest == null) {
            return TradeResponse.builder()
                    .message("매수요청 성공")
                    .tradeRequest(buyTradeRequest)
                    .build();
        }

        // 거래 성사 조건: 매수 가격이 매도 가격 이상일 때
        if (buyRequest.getUnitCost() >= sellRequest.getUnitCost()) {
            // 큐에서 제거
            buyQueue.poll();
            sellQueue.poll();

            return TradeResponse.builder()
                    .message("매수 성공")
                    .tradeRequest(buyTradeRequest)
                    .build();
        } else {
            return TradeResponse.builder()
                    .message("매수요청 성공")
                    .tradeRequest(buyTradeRequest)
                    .build();
        }
    }

    public TradeResponse matchSellTrades(SellTradeRequest sellTradeRequest) {
        String company = sellTradeRequest.getCompanyName();
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = limitBuyOrderQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = limitSellOrderQueues.get(company);

        // 우선순위 큐의 첫번째 요소를 가져오지만 제거하지 않음
        BuyTradeRequest buyRequest = buyQueue.peek();
        SellTradeRequest sellRequest = sellQueue.peek();

        if (buyRequest == null || sellRequest == null) {
            return TradeResponse.builder()
                    .message("매도요청 성공")
                    .tradeRequest(sellTradeRequest)
                    .build();
        }

        // 거래 성사 조건: 매수 가격이 매도 가격 이상일 때
        if (buyRequest.getUnitCost() >= sellRequest.getUnitCost()) {
            // 큐에서 제거
            buyQueue.poll();
            sellQueue.poll();
            return TradeResponse.builder()
                    .message("매도 성공")
                    .tradeRequest(sellTradeRequest)
                    .build();
        } else {
            return TradeResponse.builder()
                    .message("매도요청 성공")
                    .tradeRequest(sellTradeRequest)
                    .build();
        }
    }
}
