package com.sascom.chickenstock.domain.trade.util;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.error.code.AccountErrorCode;
import com.sascom.chickenstock.domain.account.error.exception.AccountNotFoundException;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.domain.trade.dto.MatchStatus;
import com.sascom.chickenstock.domain.trade.dto.ProcessedOrderDto;
import com.sascom.chickenstock.domain.trade.dto.RealStockTradeDto;
import com.sascom.chickenstock.domain.trade.dto.TradeType;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.TradeRequest;

import java.util.List;

public class ChickenStockManager implements StockManager {
    private ChickenStockQueue<SellTradeRequest> sellQueue;
    private ChickenStockQueue<BuyTradeRequest> buyQueue;
    private RedisService redisService;
    private AccountRepository accountRepository;

    public ChickenStockManager(RedisService redisService, AccountRepository accountRepository) {
        sellQueue = new ChickenStockQueueImpl<>();
        buyQueue = new ChickenStockQueueImpl<>();
        this.redisService = redisService;
        this.accountRepository = accountRepository;
    }

    @Override
    public synchronized void match(int marketPrice, List<ProcessedOrderDto> canceled, List<ProcessedOrderDto> executed) {
        SellTradeRequest sellTradeRequest = null;
        BuyTradeRequest buyTradeRequest = null;
        while(true) {
            if(sellTradeRequest == null) {
                addCanceledTradeRequestToList(
                        sellQueue.remove(marketPrice),
                        canceled,
                        TradeType.SELL,
                        MatchStatus.CANCELED_BY_LOGIC);
                sellTradeRequest = sellQueue.first(marketPrice);
            }
            if(buyTradeRequest == null) {
                addCanceledTradeRequestToList(
                        buyQueue.remove(marketPrice),
                        canceled,
                        TradeType.BUY,
                        MatchStatus.CANCELED_BY_LOGIC
                );
                buyTradeRequest = buyQueue.first(marketPrice);
            }
            if(sellTradeRequest == null || buyTradeRequest == null) {
                break;
            }

            // TODO: validate balance. AccountRepository와 Redis에 있는 미체결 정보를 통해 최대한으로 살 수 있는 개수 확인.
            // 한 주도 살 수 없다면 continue.
            // 아래는 대충 pseudocode.
            /*
            Integer realBalance = AccountService.~~~ - Redis.~~~ + marketPrice * buyTradeRequest.getRemainingVolume();
            Integer maxBuyVolume = realBalance / marketPrice;
            // 지금 체결하려는 개수(executedVolume) 만큼은 못산다면 나머지 buy 요청은 전부 취소.
            if(maxBuyVolume < executedVolume) {
                Integer canceledVolume = buyTradeRequest.getRemainingVolume() - maxBuyVolume;
                canceled.add(ProcessedOrderDto.builder()
                    .accountId(buyTradeRequest.getAccountId())
                    .requestHistoryId(buyTradeRequest.getHistoryId())
                    .companyName(buyTradeRequest.getCompanyName())
                    .price(marketPrice)
                    .volume(canceledVolume)
                    .orderType(tradeRequest.getOrderType())
                    .matchStatus(MatchStatus.CANCELED_BY_BALANCE)
                    .build());
                buyTradeRequest.addExecutedVolume(canceledVolume);
                executedVolume = maxBuyVolume
            }
            if(executedVolume == 0) {
                buyQueue.remove(buyTradeRequest);
                buyTradeRequest = null;
                continue;
            }
             */
            // buy 검증
            Account buyAccount = accountRepository.findById(buyTradeRequest.getAccountId())
                    .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
            int buyCount = (int)Math.min(buyAccount.getBalance() / marketPrice, buyTradeRequest.getRemainingVolume());
            // sell 검증
            Account sellAccount = accountRepository.findById(sellTradeRequest.getAccountId())
                    .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
            if(buyCount == 0) {
                canceled.add(canceledTradeRequestToProcessedOrderDto(buyTradeRequest, TradeType.BUY, MatchStatus.CANCELED_BY_BALANCE));
                buyQueue.remove(buyTradeRequest);
                buyTradeRequest = null;
            }
            if(sellTradeRequest == null || buyTradeRequest == null) {
                continue;
            }

            int executedVolume = Math.min(sellTradeRequest.getRemainingVolume(), buyCount);
            buyTradeRequest.addExecutedVolume(executedVolume);
            executed.add(executedTradeRequestToProcessedOrderDto(buyTradeRequest, TradeType.BUY, executedVolume, marketPrice));
            buyAccount.updateBalance(-(long)marketPrice * executedVolume);
            accountRepository.save(buyAccount);
            sellTradeRequest.addExecutedVolume(executedVolume);
            executed.add(executedTradeRequestToProcessedOrderDto(sellTradeRequest, TradeType.SELL, executedVolume, marketPrice));
            sellAccount.updateBalance(-(long)marketPrice * executedVolume);
            accountRepository.save(sellAccount);

            if(sellTradeRequest.getTotalOrderVolume().equals(sellTradeRequest.getExecutedVolume())) {
                sellQueue.remove(sellTradeRequest);
                sellTradeRequest = null;
            }
            if(buyTradeRequest.getTotalOrderVolume().equals(buyTradeRequest.getExecutedVolume())) {
                buyQueue.remove(buyTradeRequest);
                buyTradeRequest = null;
            }
        }
        accountRepository.flush();
        return;
    }

    @Override
    public synchronized void processRealStockTrade(
            RealStockTradeDto realStockTradeDto,
            List<ProcessedOrderDto> canceled,
            List<ProcessedOrderDto> executed) {
        int count = realStockTradeDto.transactionVolume();
        int price = realStockTradeDto.currentPrice();
        TradeType setType = realStockTradeDto.tradeType() == TradeType.SELL?
                TradeType.BUY : TradeType.SELL;
        if(TradeType.SELL.equals(setType)){
            while(count > 0) {
                addCanceledTradeRequestToList(
                        sellQueue.remove(price),
                        canceled,
                        TradeType.SELL,
                        MatchStatus.CANCELED_BY_LOGIC
                );
                SellTradeRequest tradeRequest = sellQueue.first(price);
                if(tradeRequest == null) {
                    break;
                }
                Account sellAccount = accountRepository.findById(tradeRequest.getAccountId())
                        .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
                int executedVolume = Math.min(count, tradeRequest.getRemainingVolume());
                count -= executedVolume;
                tradeRequest.addExecutedVolume(executedVolume);
                executed.add(executedTradeRequestToProcessedOrderDto(tradeRequest, TradeType.SELL, executedVolume, price));
                sellAccount.updateBalance((long)price * executedVolume);
                accountRepository.save(sellAccount);
                if(tradeRequest.getRemainingVolume() == 0) {
                    sellQueue.remove(tradeRequest);
                }
            }
        }
        if(TradeType.BUY.equals(setType)){
            while(count > 0) {
                addCanceledTradeRequestToList(
                        buyQueue.remove(price),
                        canceled,
                        TradeType.BUY,
                        MatchStatus.CANCELED_BY_LOGIC
                );
                BuyTradeRequest tradeRequest = buyQueue.first(price);
                if(tradeRequest == null) {
                    break;
                }
                Account buyAccount = accountRepository.findById(tradeRequest.getAccountId())
                        .orElseThrow(() -> AccountNotFoundException.of(AccountErrorCode.NOT_FOUND));
                int buyCount = (int)Math.min(buyAccount.getBalance() / price, tradeRequest.getRemainingVolume());
                if(buyCount == 0) {
                    canceledTradeRequestToProcessedOrderDto(tradeRequest, TradeType.BUY, MatchStatus.CANCELED_BY_BALANCE);
                    buyQueue.remove(tradeRequest);
                    tradeRequest = null;
                    continue;
                }
                int executedVolume = Math.min(count, buyCount);
                count -= executedVolume;
                tradeRequest.addExecutedVolume(executedVolume);
                executed.add(executedTradeRequestToProcessedOrderDto(tradeRequest, TradeType.SELL, executedVolume, price));
                buyAccount.updateBalance(-(long)price * executedVolume);
                accountRepository.save(buyAccount);
                if(tradeRequest.getRemainingVolume() == 0) {
                    buyQueue.remove(tradeRequest);
                }
            }
        }
        accountRepository.flush();
        return;
    }

//    @Override
//    public synchronized void match(int marketPrice, List<ProcessedOrderDto> canceled, List<ProcessedOrderDto> executed) {
//        while (true) {
//            addCanceledTradeRequestToList(
//                    sellQueue.remove(marketPrice),
//                    canceled,
//                    TradeType.SELL,
//                    MatchStatus.CANCELED_BY_LOGIC);
//            SellTradeRequest sellTradeRequest = sellQueue.first(marketPrice);
//            addCanceledTradeRequestToList(
//                    buyQueue.remove(marketPrice),
//                    canceled,
//                    TradeType.BUY,
//                    MatchStatus.CANCELED_BY_LOGIC
//            );
//            BuyTradeRequest buyTradeRequest = buyQueue.first(marketPrice);
//            if (sellTradeRequest == null || buyTradeRequest == null) {
//                break;
//            }
//
//            int executedVolume = Math.min(sellTradeRequest.getRemainingVolume(), buyTradeRequest.getRemainingVolume());
//            // TODO: validate balance. AccountRepository와 Redis에 있는 미체결 정보를 통해 최대한으로 살 수 있는 개수 확인.
//            // 한 주도 살 수 없다면 continue.
//            // 아래는 대충 pseudocode.
//            /*
//            Integer realBalance = AccountService.~~~ - Redis.~~~ + marketPrice * buyTradeRequest.getRemainingVolume();
//            Integer maxBuyVolume = realBalance / marketPrice;
//            // 지금 체결하려는 개수(executedVolume) 만큼은 못산다면 나머지 buy 요청은 전부 취소.
//            if(maxBuyVolume < executedVolume) {
//                Integer canceledVolume = buyTradeRequest.getRemainingVolume() - maxBuyVolume;
//                canceled.add(ProcessedOrderDto.builder()
//                    .accountId(buyTradeRequest.getAccountId())
//                    .requestHistoryId(buyTradeRequest.getHistoryId())
//                    .companyName(buyTradeRequest.getCompanyName())
//                    .price(marketPrice)
//                    .volume(canceledVolume)
//                    .orderType(tradeRequest.getOrderType())
//                    .matchStatus(MatchStatus.CANCELED_BY_BALANCE)
//                    .build());
//                buyTradeRequest.addExecutedVolume(canceledVolume);
//                executedVolume = maxBuyVolume
//            }
//            if(executedVolume == 0) {
//                buyQueue.remove(buyTradeRequest);
//                buyTradeRequest = null;
//                continue;
//            }
//             */
//            sellTradeRequest.addExecutedVolume(executedVolume);
//            executed.add(executedTradeRequestToProcessedOrderDto(sellTradeRequest, TradeType.SELL, executedVolume, marketPrice));
//            buyTradeRequest.addExecutedVolume(executedVolume);
//            executed.add(executedTradeRequestToProcessedOrderDto(buyTradeRequest, TradeType.BUY, executedVolume, marketPrice));
//
//            if (sellTradeRequest.getTotalOrderVolume().equals(sellTradeRequest.getExecutedVolume())) {
//                sellQueue.remove(sellTradeRequest);
//            }
//            if (buyTradeRequest.getTotalOrderVolume().equals(buyTradeRequest.getExecutedVolume())) {
//                buyQueue.remove(buyTradeRequest);
//            }
//        }
//        return;
//    }

    @Override
    public boolean order(SellTradeRequest tradeRequest) {
        return sellQueue.add(tradeRequest) != null;
    }

    @Override
    public boolean order(BuyTradeRequest tradeRequest) {
        return buyQueue.add(tradeRequest) != null;
    }

    @Override
    public SellTradeRequest cancel(SellTradeRequest tradeRequest) {
        return sellQueue.remove(tradeRequest);
    }

    @Override
    public BuyTradeRequest cancel(BuyTradeRequest tradeRequest) {
        return buyQueue.remove(tradeRequest);
    }

    @Override
    public void clear() {
        sellQueue.clear();
        buyQueue.clear();
    }

    @Override
    public boolean isSellQueueEmpty() {
        return sellQueue.isEmpty();
    }

    @Override
    public boolean isBuyQueueEmpty() {
        return buyQueue.isEmpty();
    }

    private void addCanceledTradeRequestToList(
            List<? extends TradeRequest> tradeRequests,
            List<ProcessedOrderDto> list,
            TradeType tradeType,
            MatchStatus matchStatus
    ) {
        for (TradeRequest tradeRequest : tradeRequests) {
            list.add(canceledTradeRequestToProcessedOrderDto(tradeRequest, tradeType, matchStatus));
        }
        return;
    }

//    private void addCanceledTradeRequestToList(
//            TradeRequest tradeRequest,
//            List<ProcessedOrderDto> list,
//            MatchStatus matchStatus
//    ) {
//        list.add(canceledTradeRequestToProcessedOrderDto(tradeRequest, matchStatus));
//    }

    private ProcessedOrderDto canceledTradeRequestToProcessedOrderDto(
            TradeRequest tradeRequest,
            TradeType tradeType,
            MatchStatus matchStatus) {
        return ProcessedOrderDto.builder()
                .accountId(tradeRequest.getAccountId())
                .requestHistoryId(tradeRequest.getHistoryId())
                .companyId(tradeRequest.getCompanyId())
                .price(tradeRequest.getUnitCost())
                .volume(tradeRequest.getRemainingVolume())
                .tradeType(tradeType)
                .orderType(tradeRequest.getOrderType())
                .matchStatus(matchStatus)
                .build();
    }

    private ProcessedOrderDto executedTradeRequestToProcessedOrderDto(
            TradeRequest tradeRequest,
            TradeType tradeType,
            int executedVolume,
            int marketPrice
    ) {
        return ProcessedOrderDto.builder()
                .accountId(tradeRequest.getAccountId())
                .requestHistoryId(tradeRequest.getHistoryId())
                .companyId(tradeRequest.getCompanyId())
                .price(marketPrice)
                .volume(executedVolume)
                .tradeType(tradeType)
                .orderType(tradeRequest.getOrderType())
                .matchStatus(MatchStatus.EXECUTED)
                .build();
    }
}
