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

    private final Map<String, PriorityBlockingQueue<BuyTradeRequest>> buyQueues;
    private final Map<String, PriorityBlockingQueue<SellTradeRequest>> sellQueues;

    @Autowired
    public TradeService(Map<String, PriorityBlockingQueue<BuyTradeRequest>> buyQueues,
                        Map<String, PriorityBlockingQueue<SellTradeRequest>> sellQueues) {
        this.buyQueues = buyQueues;
        this.sellQueues = sellQueues;
    }

    public TradeResponse addBuyRequest(BuyTradeRequest buyTradeRequest) {
        if(!buyQueues.containsKey(buyTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        buyQueues.get(buyTradeRequest.getCompanyName()).offer(buyTradeRequest);
         return matchBuyTrades(buyTradeRequest);
    }

    public TradeResponse addSellRequest(SellTradeRequest sellTradeRequest) {
        if(!sellQueues.containsKey(sellTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        sellQueues.get(sellTradeRequest.getCompanyName()).offer(sellTradeRequest);
        return matchSellTrades(sellTradeRequest);
    }

    public BuyTradeRequest processBuyRequest(String company) {
        return buyQueues.get(company).poll();
    }

    public SellTradeRequest processSellRequest(String company) {
        return sellQueues.get(company).poll();
    }

    public boolean isBuyQueueEmpty(String company) {
        return buyQueues.get(company).isEmpty();
    }

    public boolean isSellQueueEmpty(String company) {
        return sellQueues.get(company).isEmpty();
    }

    public TradeResponse matchBuyTrades(BuyTradeRequest buyTradeRequest) {
        String company = buyTradeRequest.getCompanyName();
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = buyQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = sellQueues.get(company);

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
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = buyQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = sellQueues.get(company);

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
