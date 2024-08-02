package com.sascom.chickenstock.domain.trade.config;

import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;


@Configuration
public class TradeConfig {
    @Bean(name = "limitBuyOrderQueues")
    public Map<String, PriorityBlockingQueue<BuyTradeRequest>> limitBuyOrderQueues() {
        Map<String, PriorityBlockingQueue<BuyTradeRequest>> limitBuyOrderQueues = new HashMap<>();
        limitBuyOrderQueues.put("Samsung", new PriorityBlockingQueue<>());
        limitBuyOrderQueues.put("LG", new PriorityBlockingQueue<>());
        limitBuyOrderQueues.put("Chicken Delight", new PriorityBlockingQueue<>());
        return limitBuyOrderQueues;
    }

    @Bean(name = "limitSellOrderQueues")
    public Map<String, PriorityBlockingQueue<SellTradeRequest>> limitSellOrderQueues() {
        Map<String, PriorityBlockingQueue<SellTradeRequest>> limitSellOrderQueues = new HashMap<>();
        limitSellOrderQueues.put("Samsung", new PriorityBlockingQueue<>());
        limitSellOrderQueues.put("LG", new PriorityBlockingQueue<>());
        limitSellOrderQueues.put("Chicken Delight", new PriorityBlockingQueue<>());
        return limitSellOrderQueues;
    }

    @Bean(name = "marketBuyOrderQueues")
    public Map<String, PriorityBlockingQueue<BuyTradeRequest>> marketBuyOrderQueues() {
        Map<String, PriorityBlockingQueue<BuyTradeRequest>> marketBuyOrderQueues = new HashMap<>();
        marketBuyOrderQueues.put("Samsung", new PriorityBlockingQueue<>());
        marketBuyOrderQueues.put("LG", new PriorityBlockingQueue<>());
        marketBuyOrderQueues.put("Chicken Delight", new PriorityBlockingQueue<>());
        return marketBuyOrderQueues;
    }

    @Bean(name = "marketSellOrderQueues")
    public Map<String, PriorityBlockingQueue<SellTradeRequest>> marketSellOrderQueues() {
        Map<String, PriorityBlockingQueue<SellTradeRequest>> marketSellOrderQueues = new HashMap<>();
        marketSellOrderQueues.put("Samsung", new PriorityBlockingQueue<>());
        marketSellOrderQueues.put("LG", new PriorityBlockingQueue<>());
        marketSellOrderQueues.put("Chicken Delight", new PriorityBlockingQueue<>());
        return marketSellOrderQueues;
    }

}
