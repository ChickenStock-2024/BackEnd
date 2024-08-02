package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.ProcessedOrderDto;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;

import java.util.List;

public interface StockManager {
    void match(int marketPrice, List<ProcessedOrderDto> canceled, List<ProcessedOrderDto> executed);
    boolean order(SellTradeRequest tradeRequest);
    boolean order(BuyTradeRequest tradeRequest);
    boolean cancel(SellTradeRequest tradeRequest);
    boolean cancel(BuyTradeRequest tradeRequest);
}
