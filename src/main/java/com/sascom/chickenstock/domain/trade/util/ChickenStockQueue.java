package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;


import java.util.concurrent.ConcurrentSkipListSet;

public class ChickenStockQueue<T extends TradeRequest> {
    private ConcurrentSkipListSet<T> limitQueue, marketQueue;
    public ChickenStockQueue() {
        limitQueue = new ConcurrentSkipListSet<>();
        marketQueue = new ConcurrentSkipListSet<>();
    }
    public boolean add(T tradeRequest) {
        return switch (tradeRequest.getOrderType()) {
            case LIMIT -> limitQueue.add(tradeRequest);
            case MARKET -> marketQueue.add(tradeRequest);
            default -> false;
        };
    }
    public boolean remove(T tradeRequest) {
        return switch(tradeRequest.getOrderType()) {
            case LIMIT -> limitQueue.remove(tradeRequest);
            case MARKET -> marketQueue.remove(tradeRequest);
            default -> false;
        };
    }
    public T first(int marketPrice) {
        while(!limitQueue.isEmpty() && limitQueue.first().compareByUnitCost(marketPrice) < 0) {
            limitQueue.pollFirst();
        }
        if(!limitQueue.isEmpty() && limitQueue.first().compareByUnitCost(marketPrice) > 0) {
            return marketQueue.isEmpty()? null : marketQueue.first();
        }
        if(marketQueue.isEmpty()) {
            return limitQueue.first();
        }
        int compareResult = limitQueue.first().compareByOrderTimeAndVolume(marketQueue.first());
        if(compareResult == 0) {
            // TODO: change into chickenstock exception
            throw new IllegalStateException("implementation error.");
        }
        return compareResult < 0? limitQueue.first() : marketQueue.first();
    }
}
