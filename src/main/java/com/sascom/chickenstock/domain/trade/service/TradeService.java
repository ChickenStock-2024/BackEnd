package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.BuyTradeResponse;
import com.sascom.chickenstock.domain.trade.dto.response.SellTradeResponse;
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

    public BuyTradeResponse addBuyRequest(BuyTradeRequest buyTradeRequest) {
        if(!buyQueues.containsKey(buyTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        buyQueues.get(buyTradeRequest.getCompanyName()).offer(buyTradeRequest);
         return matchBuyTrades(buyTradeRequest.getCompanyName());
    }

    public SellTradeResponse addSellRequest(SellTradeRequest sellTradeRequest) {
        if(!sellQueues.containsKey(sellTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        sellQueues.get(sellTradeRequest.getCompanyName()).offer(sellTradeRequest);
        return matchSellTrades(sellTradeRequest.getCompanyName());
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

    public BuyTradeResponse matchBuyTrades(String company) {
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = buyQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = sellQueues.get(company);

        // 우선순위 큐의 첫번째 요소를 가져오지만 제거하지 않음
        BuyTradeRequest buyRequest = buyQueue.peek();
        SellTradeRequest sellRequest = sellQueue.peek();

        if (buyRequest == null || sellRequest == null) {
            return BuyTradeResponse.builder()
                    .message("매수요청 성공")
                    .buyTradeRequest(null)
                    .build();
        }

        // 거래 성사 조건: 매수 가격이 매도 가격 이상일 때
        if (buyRequest.getUnitCost() >= sellRequest.getUnitCost()) {
            // 큐에서 제거
            buyQueue.poll();
            sellQueue.poll();

            return BuyTradeResponse.builder()
                    .message("매수 성공")
                    .buyTradeRequest(null)
                    .build();
        } else {
            return BuyTradeResponse.builder()
                    .message("매수요청 성공")
                    .buyTradeRequest(null)
                    .build();
        }
    }

    public SellTradeResponse matchSellTrades(String company) {
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = buyQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = sellQueues.get(company);

        // 우선순위 큐의 첫번째 요소를 가져오지만 제거하지 않음
        BuyTradeRequest buyRequest = buyQueue.peek();
        SellTradeRequest sellRequest = sellQueue.peek();

        if (buyRequest == null || sellRequest == null) {
            return SellTradeResponse.builder()
                    .message("매도요청 성공")
                    .sellTradeRequest(null)
                    .build();
        }

        // 거래 성사 조건: 매수 가격이 매도 가격 이상일 때
        if (buyRequest.getUnitCost() >= sellRequest.getUnitCost()) {
            // 큐에서 제거
            buyQueue.poll();
            sellQueue.poll();
            return SellTradeResponse.builder()
                    .message("매도 성공")
                    .sellTradeRequest(null)
                    .build();
        } else {
            return SellTradeResponse.builder()
                    .message("매도요청 성공")
                    .sellTradeRequest(null)
                    .build();
        }
    }
}
