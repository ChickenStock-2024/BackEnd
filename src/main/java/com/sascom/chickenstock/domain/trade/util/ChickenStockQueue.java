package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;

public interface ChickenStockQueue<T extends TradeRequest> {
    public boolean add(T tradeRequest);
    public boolean remove(T tradeRequest);
    public T first(int marketPrice);
}
