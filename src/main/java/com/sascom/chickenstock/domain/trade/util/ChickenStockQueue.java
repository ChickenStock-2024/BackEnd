package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;

import java.util.List;

public interface ChickenStockQueue<T extends TradeRequest> {
    T add(T tradeRequest);
    T remove(T tradeRequest);
    List<T> remove(int marketPrice);
    T first(int marketPrice);
    void clear();
    boolean isEmpty();
}
