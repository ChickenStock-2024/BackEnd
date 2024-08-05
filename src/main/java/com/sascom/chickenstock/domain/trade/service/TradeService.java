package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.trade.dto.OrderType;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeException;
import com.sascom.chickenstock.domain.trade.util.ChickenStockManager;
import com.sascom.chickenstock.domain.trade.util.StockManager;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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
        if(tradeRequest.getOrderType() != OrderType.LIMIT) {
            throw TradeException.of(TradeErrorCode.INVALID_ORDER);
        }
        return processBuyRequest(tradeRequest);
    }

    public TradeResponse addMarketBuyRequest(BuyTradeRequest tradeRequest) {
        if(tradeRequest.getOrderType() != OrderType.MARKET) {
            throw TradeException.of(TradeErrorCode.INVALID_ORDER);
        }
        return processBuyRequest(tradeRequest);
    }

    public TradeResponse addLimitSellRequest(SellTradeRequest tradeRequest) {
        if(tradeRequest.getOrderType() != OrderType.LIMIT) {
            throw TradeException.of(TradeErrorCode.INVALID_ORDER);
        }
        return processSellRequest(tradeRequest);
    }

    public TradeResponse addMarketSellRequest(SellTradeRequest tradeRequest) {
        if(tradeRequest.getOrderType() != OrderType.LIMIT) {
            throw TradeException.of(TradeErrorCode.INVALID_ORDER);
        }
        return processSellRequest(tradeRequest);
    }

    private TradeResponse processBuyRequest(BuyTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        boolean result = stockManager.order(tradeRequest);
        if(!result) {
            throw TradeException.of(TradeErrorCode.INTERNAL_ERROR);
        }
        return TradeResponse.builder()
                .message("매수 요청 완료")
                .tradeRequest(tradeRequest)
                .build();
    }

    private TradeResponse processSellRequest(SellTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        boolean result = stockManager.order(tradeRequest);
        if(!result) {
            throw TradeException.of(TradeErrorCode.INTERNAL_ERROR);
        }
        return TradeResponse.builder()
                .message("매도 요청 완료")
                .tradeRequest(tradeRequest)
                .build();
    }

    private Optional<StockManager> getStockManagerByCompanyName(String companyName) {
        return Optional.of(stockManagerMap.get(companyName));
    }
}