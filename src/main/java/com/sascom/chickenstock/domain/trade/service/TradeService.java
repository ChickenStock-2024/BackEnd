package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
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

    public void addBuyRequest(BuyTradeRequest buyTradeRequest) {
        buyQueues.get(buyTradeRequest.getCompanyName()).offer(buyTradeRequest);
    }

    public void addSellRequest(SellTradeRequest sellTradeRequest) {
        sellQueues.get(sellTradeRequest.getCompanyName()).offer(sellTradeRequest);
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

    public String matchTrades(String company) {
        PriorityBlockingQueue<BuyTradeRequest> buyQueue = buyQueues.get(company);
        PriorityBlockingQueue<SellTradeRequest> sellQueue = sellQueues.get(company);

        // 우선순위 큐의 첫번째 요소를 가져오지만 제거하지 않음
        BuyTradeRequest buyRequest = buyQueue.peek();
        SellTradeRequest sellRequest = sellQueue.peek();

        if (buyRequest == null || sellRequest == null) {
            return "No matching trades available";
        }

        // 거래 성사 조건: 매수 가격이 매도 가격 이상일 때
        if (buyRequest.getUnitCost() >= sellRequest.getUnitCost()) {
            // 큐에서 제거
            buyQueue.poll();
            sellQueue.poll();
            return "Trade matched: Buy " + buyRequest + " with Sell " + sellRequest;
        } else {
            return "No matching trades available";
        }
    }
}
