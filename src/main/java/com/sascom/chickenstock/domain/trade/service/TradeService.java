package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

@Transactional
@Service
public class TradeService {

    private final Map<String, ConcurrentSkipListSet<BuyTradeRequest>> limitBuyOrderSets;
    private final Map<String, ConcurrentSkipListSet<SellTradeRequest>> limitSellOrderSets;
    private final Map<String, ConcurrentSkipListSet<BuyTradeRequest>> marketBuyOrderSets;
    private final Map<String, ConcurrentSkipListSet<SellTradeRequest>> marketSellOrderSets;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    public TradeService(Map<String, ConcurrentSkipListSet<BuyTradeRequest>> limitBuyOrderSets,
                        Map<String, ConcurrentSkipListSet<SellTradeRequest>> limitSellOrderSets,
                        Map<String, ConcurrentSkipListSet<BuyTradeRequest>> marketBuyOrderSets,
                        Map<String, ConcurrentSkipListSet<SellTradeRequest>> marketSellOrderSets) {
        this.limitBuyOrderSets = limitBuyOrderSets;
        this.limitSellOrderSets = limitSellOrderSets;
        this.marketBuyOrderSets = marketBuyOrderSets;
        this.marketSellOrderSets = marketSellOrderSets;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        for (String company : limitBuyOrderSets.keySet()) {
            scheduleTradeMatching(company);
        }
    }

    private void scheduleTradeMatching(String company) {
        taskScheduler.scheduleAtFixedRate(() -> matchTrades(company), Duration.ofMillis(100));
    }

    private void matchTrades(String company) {
        ConcurrentSkipListSet<BuyTradeRequest> buySet = limitBuyOrderSets.get(company);
        ConcurrentSkipListSet<SellTradeRequest> sellSet = limitSellOrderSets.get(company);

        while (!buySet.isEmpty() && !sellSet.isEmpty()) {
            BuyTradeRequest buyRequest = buySet.first();
            SellTradeRequest sellRequest = sellSet.first();

            if (buyRequest.getUnitCost() == sellRequest.getUnitCost()) {
                buySet.remove(buyRequest);
                sellSet.remove(sellRequest);
                processTransaction(buyRequest, sellRequest);
            } else {
                break;
            }
        }
    }

    private void processTransaction(BuyTradeRequest buyRequest, SellTradeRequest sellRequest) {
        // 실제 거래 처리 로직 구현
        System.out.println("거래 성사: " + buyRequest + " - " + sellRequest);
    }

    public TradeResponse addBuyRequest(BuyTradeRequest buyTradeRequest) {
        if(!limitBuyOrderSets.containsKey(buyTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        limitBuyOrderSets.get(buyTradeRequest.getCompanyName()).add(buyTradeRequest);
        return TradeResponse.builder()
                .message("매수요청 성공")
                .tradeRequest(buyTradeRequest)
                .build();
    }

    public TradeResponse addSellRequest(SellTradeRequest sellTradeRequest) {
        if(!limitSellOrderSets.containsKey(sellTradeRequest.getCompanyName())) {
            throw TradeNotFoundException.of(TradeErrorCode.NOT_FOUND);
        }

        limitSellOrderSets.get(sellTradeRequest.getCompanyName()).add(sellTradeRequest);
        return TradeResponse.builder()
                .message("매도요청 성공")
                .tradeRequest(sellTradeRequest)
                .build();
    }

    public BuyTradeRequest processBuyRequest(String company) {
        ConcurrentSkipListSet<BuyTradeRequest> buySet = limitBuyOrderSets.get(company);
        BuyTradeRequest firstRequest = buySet.first();
        buySet.remove(firstRequest);
        return firstRequest;
    }

    public SellTradeRequest processSellRequest(String company) {
        ConcurrentSkipListSet<SellTradeRequest> sellSet = limitSellOrderSets.get(company);
        SellTradeRequest firstRequest = sellSet.first();
        sellSet.remove(firstRequest);
        return firstRequest;
    }

    public boolean isBuySetEmpty(String company) {
        return limitBuyOrderSets.get(company).isEmpty();
    }

    public boolean isSellSetEmpty(String company) {
        return limitSellOrderSets.get(company).isEmpty();
    }
}