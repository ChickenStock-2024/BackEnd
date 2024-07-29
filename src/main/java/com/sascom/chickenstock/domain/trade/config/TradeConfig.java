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
    @Bean
    public Map<String, PriorityBlockingQueue<BuyTradeRequest>> buyQueues() {
        Map<String, PriorityBlockingQueue<BuyTradeRequest>> buyQueues = new HashMap<>();
        buyQueues.put("Samsung", new PriorityBlockingQueue<>());
        buyQueues.put("LG", new PriorityBlockingQueue<>());
        buyQueues.put("Chicken Delight", new PriorityBlockingQueue<>());
        return buyQueues;
    }

    @Bean
    public Map<String, PriorityBlockingQueue<SellTradeRequest>> sellQueues() {
        Map<String, PriorityBlockingQueue<SellTradeRequest>> sellQueues = new HashMap<>();
        sellQueues.put("Samsung", new PriorityBlockingQueue<>());
        sellQueues.put("LG", new PriorityBlockingQueue<>());
        sellQueues.put("Chicken Delight", new PriorityBlockingQueue<>());
        return sellQueues;
    }

}
