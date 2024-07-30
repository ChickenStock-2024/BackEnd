package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class TradeService {

    private final Map<String, ConcurrentSkipListSet<BuyTradeRequest>> limitBuyOrderSets;
    private final Map<String, ConcurrentSkipListSet<SellTradeRequest>> limitSellOrderSets;
    private final Map<String, ConcurrentSkipListSet<BuyTradeRequest>> marketBuyOrderSets;
    private final Map<String, ConcurrentSkipListSet<SellTradeRequest>> marketSellOrderSets;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    public TradeService(@Qualifier("limitBuyOrderSets") Map<String, ConcurrentSkipListSet<BuyTradeRequest>> limitBuyOrderSets,
                        @Qualifier("limitSellOrderSets") Map<String, ConcurrentSkipListSet<SellTradeRequest>> limitSellOrderSets,
                        @Qualifier("marketBuyOrderSets") Map<String, ConcurrentSkipListSet<BuyTradeRequest>> marketBuyOrderSets,
                        @Qualifier("marketSellOrderSets") Map<String, ConcurrentSkipListSet<SellTradeRequest>> marketSellOrderSets) {
        this.limitBuyOrderSets = limitBuyOrderSets;
        this.limitSellOrderSets = limitSellOrderSets;
        this.marketBuyOrderSets = marketBuyOrderSets;
        this.marketSellOrderSets = marketSellOrderSets;
    }

    // 기존의 addLimitBuyRequest, addLimitSellRequest, addMarketBuyRequest, addMarketSellRequest 메서드는 그대로 유지

    @PostConstruct
    public void init() {
        for (String company : limitBuyOrderSets.keySet()) {
            scheduleTradeMatching(company);
        }
    }

    private void scheduleTradeMatching(String company) {
        taskScheduler.scheduleAtFixedRate(() -> matchTrades(company), Duration.ofMillis(100));
    }

    private void matchTrades(String company) {
        ConcurrentSkipListSet<BuyTradeRequest> A = limitBuyOrderSets.get(company);
        ConcurrentSkipListSet<SellTradeRequest> C = limitSellOrderSets.get(company);
        ConcurrentSkipListSet<BuyTradeRequest> B = marketBuyOrderSets.get(company);
        ConcurrentSkipListSet<SellTradeRequest> D = marketSellOrderSets.get(company);

        boolean isA = A.isEmpty();
        boolean isB = B.isEmpty();
        boolean isC = C.isEmpty();
        boolean isD = D.isEmpty();
        boolean isBuyAvailable = !isA || !isB;
        boolean isSellAvailable = !isC || !isD;

        // (임시) 시장가
        int nowPrice = 80000;

        while(isBuyAvailable && isSellAvailable) {
            BuyTradeRequest buyOrder = null;
            SellTradeRequest sellOrder = null;

            // 매도 주문 선택
            if (!isA && !isB) {
                if (A.first().getUnitCost() >= nowPrice) { // A가 시장가이고
                    if (A.first().getOrderTime().isBefore(B.first().getOrderTime())) {
                        buyOrder = A.first();  // A가 더 빨리온 거면
                    } else {
                        buyOrder = B.first(); // B가 더 빨리온 거면
                    }
                }
                else { // 매도 큐의 맨 앞 녀석이 시장가보다 비쌀 때
                    buyOrder = B.first(); // B쪽 거래 시키고
                    // A 퇴갤시킴

                }
            } else if (!isA) {
                buyOrder = A.first();
            } else if (!isB) {
                buyOrder = B.first();
            }

            // 매도 주문 선택
            if (!isC && !isD) {
                if (C.first().getUnitCost() <= nowPrice) {
                    if (C.first().getOrderTime().isAfter(D.first().getOrderTime())) {
                        sellOrder = C.first();
                    } else {
                        sellOrder = D.first();
                    }
                } else {
                    sellOrder = D.first();
                }
            } else if (!isC) {
                sellOrder = C.first();
            } else if (!isD) {
                sellOrder = D.first();
            }

            // 거래 실행
            if (buyOrder != null && sellOrder != null) {
                if (canTrade(buyOrder, sellOrder, nowPrice)) {
                    executeTrade(buyOrder, sellOrder, nowPrice);
                } else {
                    break;
                }
            } else {
                break;
            }

            // 상태 업데이트
            isA = A.isEmpty();
            isB = B.isEmpty();
            isC = C.isEmpty();
            isD = D.isEmpty();
            isBuyAvailable = !isA || !isB;
            isSellAvailable = !isC || !isD;
        }
    }

    private boolean canTrade(BuyTradeRequest buyOrder, SellTradeRequest sellOrder, int marketPrice) {
        if (buyOrder.getUnitCost() == null || sellOrder.getUnitCost() == null) {
            return true; // 시장가 주문은 항상 거래 가능
        }
        return buyOrder.getUnitCost() >= sellOrder.getUnitCost();
    }

    private void executeTrade(BuyTradeRequest buyOrder, SellTradeRequest sellOrder, int marketPrice) {
        int tradeAmount = Math.min(buyOrder.getAmount(), sellOrder.getAmount());
        int tradePrice = determineTradePrice(buyOrder, sellOrder, marketPrice);

        // 거래 실행 로직
        updateOrder(buyOrder, tradeAmount);
        updateOrder(sellOrder, tradeAmount);

        // 거래 기록 및 계좌 업데이트 로직 추가 필요
        System.out.println("Trade executed: " + tradeAmount + " shares at " + tradePrice);
    }

    private int determineTradePrice(BuyTradeRequest buyOrder, SellTradeRequest sellOrder, int marketPrice) {
        if (buyOrder.getUnitCost() == null || sellOrder.getUnitCost() == null) {
            return marketPrice;
        }
        return Math.max(buyOrder.getUnitCost(), sellOrder.getUnitCost());
    }

    private void updateOrder(TradeRequest order, int tradeAmount) {
        order.setAmount(order.getAmount() - tradeAmount);
        if (order.getAmount() == 0) {
            removeOrderFromSet(order);
        }
    }

    private void removeOrderFromSet(TradeRequest order) {
        String company = order.getCompanyName();
        if (order.getUnitCost() == null) {
            if (order instanceof BuyTradeRequest) {
                marketBuyOrderSets.get(company).remove(order);
            } else {
                marketSellOrderSets.get(company).remove(order);
            }
        } else {
            if (order instanceof BuyTradeRequest) {
                limitBuyOrderSets.get(company).remove(order);
            } else {
                limitSellOrderSets.get(company).remove(order);
            }
        }
    }

    // matchBuyTrades와 matchSellTrades 메서드는 그대로 유지
}