package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
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

    public TradeResponse addLimitBuyRequest(BuyTradeRequest tradeRequest) {
        return null;
    }
    public TradeResponse addMarketBuyRequest(BuyTradeRequest tradeRequest) {
        return null;
    }
    public TradeResponse addLimitSellRequest(SellTradeRequest tradeRequest) {
        return null;
    }
    public TradeResponse addMarketSellRequest(SellTradeRequest tradeRequest) {
        return null;
    }
}