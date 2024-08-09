package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.ProcessedOrderDto;
import com.sascom.chickenstock.domain.trade.dto.RealStockTradeDto;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;

import java.util.List;

public interface StockManager {
    void match(int marketPrice, List<ProcessedOrderDto> canceled, List<ProcessedOrderDto> executed);
    void processRealStockTrade(
            RealStockTradeDto realStockTradeDto,
            List<ProcessedOrderDto> canceled,
            List<ProcessedOrderDto> executed);
    boolean order(SellTradeRequest tradeRequest);
    boolean order(BuyTradeRequest tradeRequest);
    SellTradeRequest cancel(SellTradeRequest tradeRequest);
    BuyTradeRequest cancel(BuyTradeRequest tradeRequest);
    void clear();
    boolean isSellQueueEmpty();
    boolean isBuyQueueEmpty();
}
