package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeNotFoundException;
import com.sascom.chickenstock.domain.trade.util.ChickenStockManager;
import com.sascom.chickenstock.domain.trade.util.StockManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class TradeService {

    private final Map<String, StockManager> stockManagerMap;
    private final Map<String, Integer> marketPriceMap;

    public TradeService() {
        stockManagerMap = new ConcurrentHashMap<>();
        marketPriceMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void init() {
        stockManagerMap.put("삼성전자", new ChickenStockManager());
        marketPriceMap.put("삼성전자", 75700);
    }

    public TradeResponse addLimitBuyRequest(BuyTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeNotFoundException.of(TradeErrorCode.NOT_FOUND));
        return null;
    }

    public TradeResponse addMarketBuyRequest(BuyTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeNotFoundException.of(TradeErrorCode.NOT_FOUND));
        return null;
    }

    public TradeResponse addLimitSellRequest(SellTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeNotFoundException.of(TradeErrorCode.NOT_FOUND));
        return null;
    }

    public TradeResponse addMarketSellRequest(SellTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeNotFoundException.of(TradeErrorCode.NOT_FOUND));
        return null;
    }

    private Optional<StockManager> getStockManagerByCompanyName(String companyName) {
        return Optional.of(stockManagerMap.get(companyName));
    }
}