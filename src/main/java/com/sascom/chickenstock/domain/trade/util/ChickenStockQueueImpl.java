package com.sascom.chickenstock.domain.trade.util;

import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ChickenStockQueueImpl<T extends TradeRequest> implements ChickenStockQueue<T> {
    private ConcurrentSkipListSet<T> limitQueue, marketQueue;
    public ChickenStockQueueImpl() {
        limitQueue = new ConcurrentSkipListSet<>();
        marketQueue = new ConcurrentSkipListSet<>();
    }
    public T add(T tradeRequest) {
        boolean result = false;
        switch (tradeRequest.getOrderType()) {
            case LIMIT:
                result = limitQueue.add(tradeRequest);
                break;
            case MARKET:
                result = marketQueue.add(tradeRequest);
                break;
            default:
        }
        return result? tradeRequest : null;
    }
    public T remove(T tradeRequest) {
        T result = null;
        switch(tradeRequest.getOrderType()) {
            case LIMIT:
                result = limitQueue.floor(tradeRequest);
                break;
            case MARKET:
                result = marketQueue.floor(tradeRequest);
                break;
            default:
        }
        if (tradeRequest.equals(result)) {
            switch(tradeRequest.getOrderType()) {
                case LIMIT:
                    limitQueue.remove(result);
                    break;
                case MARKET:
                    marketQueue.remove(result);
                    break;
                default:
            }
        }
        else {
            result = null;
        }
        return result;
    }

    @Override
    public List<T> remove(int marketPrice) {
        List<T> resultList = new ArrayList<>();
        while(!limitQueue.isEmpty() && limitQueue.first().compareByUnitCost(marketPrice) < 0) {
            resultList.add(limitQueue.pollFirst());
        }
        return resultList;
    }

    @Override
    public T first(int marketPrice) {
        if(limitQueue.isEmpty() || limitQueue.first().compareByUnitCost(marketPrice) > 0) {
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

    @Override
    public void clear() {
        limitQueue.clear();
        marketQueue.clear();
    }

    @Override
    public boolean isEmpty() {
        return limitQueue.isEmpty() && marketQueue.isEmpty();
    }
}
