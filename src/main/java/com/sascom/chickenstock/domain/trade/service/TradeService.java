package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.trade.dto.OrderType;
import com.sascom.chickenstock.domain.trade.dto.ProcessedOrderDto;
import com.sascom.chickenstock.domain.trade.dto.TradeType;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.CancelOrderResponse;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeException;
import com.sascom.chickenstock.domain.trade.util.ChickenStockManager;
import com.sascom.chickenstock.domain.trade.util.StockManager;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TradeService {

    private final Map<String, StockManager> stockManagerMap;
    private final Map<String, Integer> marketPriceMap;
    private final HistoryRepository historyRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;

    @Autowired
    public TradeService(
            HistoryRepository historyRepository,
            AccountRepository accountRepository,
            CompanyRepository companyRepository) {
        stockManagerMap = new ConcurrentHashMap<>();
        marketPriceMap = new ConcurrentHashMap<>();
        this.historyRepository = historyRepository;
        this.accountRepository = accountRepository;
        this.companyRepository = companyRepository;
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
        if(tradeRequest.getOrderType() != OrderType.MARKET) {
            throw TradeException.of(TradeErrorCode.INVALID_ORDER);
        }
        return processSellRequest(tradeRequest);
    }

    public CancelOrderResponse cancelOrderRequest(SellTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        SellTradeRequest poppedRequest = stockManager.cancel(tradeRequest);
        if(poppedRequest == null) {
            // 분명 확인하고 요청 넣었는데 무슨일일까? 동시성? 아니면 누락?
            throw new IllegalStateException("there is no request in queue");
        }
        History history = History.builder()
                .account(accountRepository.getReferenceById(poppedRequest.getAccountId()))
                .company(companyRepository.getReferenceById(poppedRequest.getCompanyId()))
                .price(poppedRequest.getUnitCost())
                .volume(poppedRequest.getRemainingVolume())
                .status(OrderType.LIMIT.equals(tradeRequest.getOrderType())?
                        HistoryStatus.지정가매도취소 :
                        HistoryStatus.시장가매도취소)
                .build();
        historyRepository.save(history);
        return CancelOrderResponse.builder()
                .accountId(poppedRequest.getAccountId())
                .memberId(poppedRequest.getMemberId())
                .companyId(poppedRequest.getCompanyId())
                .competitionId(poppedRequest.getCompetitionId())
                .companyName(poppedRequest.getCompanyName())
                .totalOrderVolume(poppedRequest.getTotalOrderVolume())
                .executedVolume(poppedRequest.getExecutedVolume())
                .cancelVolume(poppedRequest.getRemainingVolume())
                .cancelTime(history.getCreatedAt())
                .build();
    }

    public CancelOrderResponse cancelOrderRequest(BuyTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        BuyTradeRequest poppedRequest = stockManager.cancel(tradeRequest);
        if(poppedRequest == null) {
            // 분명 확인하고 요청 넣었는데 무슨일일까? 동시성? 아니면 누락?
            throw new IllegalStateException("there is no request in queue");
        }
        History history = History.builder()
                .account(accountRepository.getReferenceById(poppedRequest.getAccountId()))
                .company(companyRepository.getReferenceById(poppedRequest.getCompanyId()))
                .price(poppedRequest.getUnitCost())
                .volume(poppedRequest.getRemainingVolume())
                .status(OrderType.LIMIT.equals(tradeRequest.getOrderType())?
                        HistoryStatus.지정가매수취소 :
                        HistoryStatus.시장가매수취소)
                .build();
        historyRepository.save(history);
        return CancelOrderResponse.builder()
                .accountId(poppedRequest.getAccountId())
                .memberId(poppedRequest.getMemberId())
                .companyId(poppedRequest.getCompanyId())
                .competitionId(poppedRequest.getCompetitionId())
                .companyName(poppedRequest.getCompanyName())
                .totalOrderVolume(poppedRequest.getTotalOrderVolume())
                .executedVolume(poppedRequest.getExecutedVolume())
                .cancelVolume(poppedRequest.getRemainingVolume())
                .cancelTime(history.getCreatedAt())
                .build();
    }

    private TradeResponse processBuyRequest(BuyTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyName(tradeRequest.getCompanyName())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        boolean result = stockManager.order(tradeRequest);
        if(!result) {
            throw TradeException.of(TradeErrorCode.INTERNAL_ERROR);
        }
        matchAndSaveHistories(stockManager, marketPriceMap.get(tradeRequest.getCompanyName()));
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
        matchAndSaveHistories(stockManager, marketPriceMap.get(tradeRequest.getCompanyName()));
        return TradeResponse.builder()
                .message("매도 요청 완료")
                .tradeRequest(tradeRequest)
                .build();
    }

    private void matchAndSaveHistories(StockManager stockManager, int marketPrice) {
        List<ProcessedOrderDto> canceled = new ArrayList<>(), executed = new ArrayList<>();
        stockManager.match(marketPrice, canceled, executed);
        historyRepository.saveAll(canceled.stream().map(order ->
                        History.builder()
                                .account(accountRepository.getReferenceById(order.accountId()))
                                .company(companyRepository.getReferenceById(order.companyId()))
                                .price(order.price())
                                .volume(order.volume())
                                .status(toCanceledHistoryStatus(order.tradeType(), order.orderType()))
                                .build())
                .toList());
        historyRepository.saveAll(executed.stream().map(order ->
                        History.builder()
                                .account(accountRepository.getReferenceById(order.accountId()))
                                .company(companyRepository.getReferenceById(order.companyId()))
                                .price(order.price())
                                .volume(order.volume())
                                .status(toExecutedHistoryStatus(order.tradeType(), order.orderType()))
                                .build())
                .toList());
        return;
    }

    private Optional<StockManager> getStockManagerByCompanyName(String companyName) {
        return Optional.of(stockManagerMap.get(companyName));
    }

    private HistoryStatus toCanceledHistoryStatus(TradeType tradeType, OrderType orderType) {
        switch(orderType) {
            case LIMIT:
                return tradeType == TradeType.SELL? HistoryStatus.지정가매도취소 : HistoryStatus.지정가매수취소;
            case MARKET:
                return tradeType == TradeType.SELL? HistoryStatus.시장가매도취소 : HistoryStatus.시장가매수취소;
            default:
                throw new IllegalStateException("server logic error");
        }
    }

    private HistoryStatus toExecutedHistoryStatus(TradeType tradeType, OrderType orderType) {
        switch(orderType) {
            case LIMIT:
                return tradeType == TradeType.SELL? HistoryStatus.지정가매도체결 : HistoryStatus.지정가매수체결;
            case MARKET:
                return tradeType == TradeType.SELL? HistoryStatus.시장가매도체결 : HistoryStatus.시장가매수체결;
            default:
                throw new IllegalStateException("server logic error");
        }
    }

}