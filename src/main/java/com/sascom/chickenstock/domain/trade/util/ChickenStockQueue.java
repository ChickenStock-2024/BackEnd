package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;

import java.util.List;

public interface ChickenStockQueue<T extends TradeRequest> {
    public T add(T tradeRequest);
    public T remove(T tradeRequest);
    public List<T> remove(int marketPrice);
    public T first(int marketPrice);
    public void clear();
    public boolean isEmpty();
}
