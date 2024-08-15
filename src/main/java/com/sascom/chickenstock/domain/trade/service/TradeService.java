package com.sascom.chickenstock.domain.trade.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.account.service.RedisService;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.dailystockprice.repository.DailyStockPriceRepository;
import com.sascom.chickenstock.domain.dailystockprice.service.DailyStockPriceService;
import com.sascom.chickenstock.domain.fcmtoken.repository.FcmTokenRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.trade.dto.*;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import com.sascom.chickenstock.domain.trade.dto.response.CancelOrderResponse;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import com.sascom.chickenstock.domain.trade.error.code.TradeErrorCode;
import com.sascom.chickenstock.domain.trade.error.exception.TradeException;
import com.sascom.chickenstock.domain.trade.util.ChickenStockManager;
import com.sascom.chickenstock.domain.trade.util.StockManager;
import com.sascom.chickenstock.global.kafkaproducer.KafkaProducer;
import com.sascom.chickenstock.global.kafkaproducer.dto.NotificationMessageDto;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TradeService {

    private final Map<Long, StockManager> stockManagerMap;
    private final Map<Long, Integer> marketPriceMap;
    private final RedisService redisService;
    private final DailyStockPriceService dailyStockPriceService;
    private final KafkaProducer kafkaProducer;
    private final HistoryRepository historyRepository;
    private final AccountRepository accountRepository;
    private final CompanyRepository companyRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Autowired
    public TradeService(
            RedisService redisService,
            DailyStockPriceService dailyStockPriceService,
            KafkaProducer kafkaProducer,
            HistoryRepository historyRepository,
            AccountRepository accountRepository,
            CompanyRepository companyRepository,
            FcmTokenRepository fcmTokenRepository) {
        stockManagerMap = new ConcurrentHashMap<>();
        marketPriceMap = new ConcurrentHashMap<>();
        this.redisService = redisService;
        this.dailyStockPriceService = dailyStockPriceService;
        this.kafkaProducer = kafkaProducer;
        this.historyRepository = historyRepository;
        this.accountRepository = accountRepository;
        this.companyRepository = companyRepository;
        this.fcmTokenRepository = fcmTokenRepository;
    }

    @PostConstruct
    public void init() {
        List<Company> companyList = companyRepository.findAll();
        for(Company company : companyList) {
            stockManagerMap.put(company.getId(), new ChickenStockManager(redisService, accountRepository));
            marketPriceMap.put(company.getId(), dailyStockPriceService.getLatestClosingPriceByCompanyId(company.getId()).intValue());
        }
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
        StockManager stockManager = getStockManagerByCompanyId(tradeRequest.getCompanyId())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        SellTradeRequest poppedRequest = stockManager.cancel(tradeRequest);
        if(poppedRequest == null) {
            throw TradeException.of(TradeErrorCode.ORDER_NOT_FOUND);
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
        StockManager stockManager = getStockManagerByCompanyId(tradeRequest.getCompanyId())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        BuyTradeRequest poppedRequest = stockManager.cancel(tradeRequest);
        if(poppedRequest == null) {
            throw TradeException.of(TradeErrorCode.ORDER_NOT_FOUND);
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
        StockManager stockManager = getStockManagerByCompanyId(tradeRequest.getCompanyId())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        boolean result = stockManager.order(tradeRequest);
        if(!result) {
            throw TradeException.of(TradeErrorCode.INTERNAL_ERROR);
        }
        redisService.setUnexecution(
                tradeRequest.getHistoryId(),
                tradeRequest.getAccountId(),
                tradeRequest.getCompanyId(),
                TradeType.BUY,
                tradeRequest.getTotalOrderVolume(),
                tradeRequest.getUnitCost());
        matchAndSaveHistories(stockManager, marketPriceMap.get(tradeRequest.getCompanyId()));
        return TradeResponse.builder()
                .message("매수 요청 완료")
                .tradeRequest(tradeRequest)
                .build();
    }

    private TradeResponse processSellRequest(SellTradeRequest tradeRequest) {
        StockManager stockManager = getStockManagerByCompanyId(tradeRequest.getCompanyId())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        boolean result = stockManager.order(tradeRequest);
        if(!result) {
            throw TradeException.of(TradeErrorCode.INTERNAL_ERROR);
        }
        redisService.setUnexecution(
                tradeRequest.getHistoryId(),
                tradeRequest.getAccountId(),
                tradeRequest.getCompanyId(),
                TradeType.SELL,
                tradeRequest.getTotalOrderVolume(),
                tradeRequest.getUnitCost());
        matchAndSaveHistories(stockManager, marketPriceMap.get(tradeRequest.getCompanyId()));
        return TradeResponse.builder()
                .message("매도 요청 완료")
                .tradeRequest(tradeRequest)
                .build();
    }

    public void processRealStockTrade(RealStockTradeDto realStockTradeDto) {
        marketPriceMap.put(realStockTradeDto.companyId(),
                TradeType.BUY.equals(realStockTradeDto.tradeType())?
                        buyHokaToMarketPrice(realStockTradeDto.currentPrice()) : realStockTradeDto.currentPrice());
        StockManager stockManager = getStockManagerByCompanyId(realStockTradeDto.companyId())
                .orElseThrow(() -> TradeException.of(TradeErrorCode.COMPANY_NOT_FOUND));
        List<ProcessedOrderDto> canceled = new ArrayList<>(), executed = new ArrayList<>();
        stockManager.processRealStockTrade(realStockTradeDto, canceled, executed);
        for(ProcessedOrderDto processedOrderDto : canceled) {
            redisService.deleteUnexecution(processedOrderDto.requestHistoryId(), processedOrderDto.accountId());;
        }
        historyRepository.saveAll(canceled.stream().map(order ->
                        History.builder()
                                .account(accountRepository.getReferenceById(order.accountId()))
                                .company(companyRepository.getReferenceById(order.companyId()))
                                .price(order.price())
                                .volume(order.volume())
                                .status(toCanceledHistoryStatus(order.tradeType(), order.orderType()))
                                .build())
                .toList());
        for(ProcessedOrderDto processedOrderDto : executed) {
            switch(processedOrderDto.tradeType()) {
                case BUY:
                    redisService.updateStockInfo(
                            processedOrderDto.accountId(),
                            processedOrderDto.companyId(),
                            processedOrderDto.volume(),
                            processedOrderDto.price() * processedOrderDto.volume()
                    );
                    kafkaProducer.sendAlarmMessageToKafka(
                            new NotificationMessageDto(
                                    fcmTokenRepository.findByMemberId(
                                                    accountRepository.findById(processedOrderDto.accountId())
                                                            .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND))
                                                            .getId())
                                            .orElseThrow(() -> new IllegalStateException("fcm error"))
                                            .getToken(),
                                    "요청이 취소되었습니다.",
                                    "")
                    );
                    break;
                case SELL:
                    redisService.updateStockInfo(
                            processedOrderDto.accountId(),
                            processedOrderDto.companyId(),
                            -processedOrderDto.volume(),
                            -processedOrderDto.price() * processedOrderDto.volume()
                    );
                    kafkaProducer.sendAlarmMessageToKafka(
                            new NotificationMessageDto(
                                    fcmTokenRepository.findByMemberId(
                                                    accountRepository.findById(processedOrderDto.accountId())
                                                            .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND))
                                                            .getId())
                                            .orElseThrow(() -> new IllegalStateException("fcm error"))
                                            .getToken(),
                                    "요청이 취소되었습니다.",
                                    "")
                    );
                    break;
            }
        }
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

    private void matchAndSaveHistories(StockManager stockManager, int marketPrice) {
        List<ProcessedOrderDto> canceled = new ArrayList<>(), executed = new ArrayList<>();
        stockManager.match(marketPrice, canceled, executed);
        for(ProcessedOrderDto processedOrderDto : canceled) {
            redisService.deleteUnexecution(processedOrderDto.requestHistoryId(), processedOrderDto.accountId());
            kafkaProducer.sendAlarmMessageToKafka(
                    new NotificationMessageDto(
                            fcmTokenRepository.findByMemberId(
                                accountRepository.findById(processedOrderDto.accountId())
                                        .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND))
                                        .getId())
                                    .orElseThrow(() -> new IllegalStateException("fcm error"))
                                    .getToken(),
                            "요청이 취소되었습니다.",
                            "")
            );
        }
        historyRepository.saveAll(canceled.stream().map(order ->
                        History.builder()
                                .account(accountRepository.getReferenceById(order.accountId()))
                                .company(companyRepository.getReferenceById(order.companyId()))
                                .price(order.price())
                                .volume(order.volume())
                                .status(toCanceledHistoryStatus(order.tradeType(), order.orderType()))
                                .build())
                .toList());
        for(ProcessedOrderDto processedOrderDto : executed) {
            switch(processedOrderDto.tradeType()) {
                case BUY:
                    redisService.updateStockInfo(
                            processedOrderDto.accountId(),
                            processedOrderDto.companyId(),
                            processedOrderDto.volume(),
                            processedOrderDto.price() * processedOrderDto.volume()
                    );
                    kafkaProducer.sendAlarmMessageToKafka(
                            new NotificationMessageDto(
                                    fcmTokenRepository.findByMemberId(
                                                    accountRepository.findById(processedOrderDto.accountId())
                                                            .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND))
                                                            .getId())
                                            .orElseThrow(() -> new IllegalStateException("fcm error"))
                                            .getToken(),
                                    "매도 요청이 체결되었습니다.",
                                    companyRepository.findById(processedOrderDto.companyId())
                                            .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND))
                                            .getName() + " " + processedOrderDto.volume() + "주"
                            )
                    );
                    break;
                case SELL:
                    redisService.updateStockInfo(
                            processedOrderDto.accountId(),
                            processedOrderDto.companyId(),
                            -processedOrderDto.volume(),
                            -processedOrderDto.price() * processedOrderDto.volume()
                    );
                    kafkaProducer.sendAlarmMessageToKafka(
                            new NotificationMessageDto(
                                    fcmTokenRepository.findByMemberId(
                                                    accountRepository.findById(processedOrderDto.accountId())
                                                            .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND))
                                                            .getId())
                                            .orElseThrow(() -> new IllegalStateException("fcm error"))
                                            .getToken(),
                                    "매수 요청이 체결되었습니다.",
                                    companyRepository.findById(processedOrderDto.companyId())
                                            .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND))
                                            .getName() + " " +
                                            processedOrderDto.volume() + "주 "
                            )
                    );
                    break;
            }
        }
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

    private Optional<StockManager> getStockManagerByCompanyId(Long companyId) {
        return Optional.of(stockManagerMap.get(companyId));
    }

    private HistoryStatus toCanceledHistoryStatus(TradeType tradeType, OrderType orderType) {
        switch(orderType) {
            case LIMIT:
                return tradeType == TradeType.SELL? HistoryStatus.지정가매도취소 : HistoryStatus.지정가매수취소;
            case MARKET:
                return tradeType == TradeType.SELL? HistoryStatus.시장가매도취소 : HistoryStatus.시장가매수취소;
            default:
                throw TradeException.of(TradeErrorCode.SERVER_ERROR);
        }
    }

    private HistoryStatus toExecutedHistoryStatus(TradeType tradeType, OrderType orderType) {
        switch(orderType) {
            case LIMIT:
                return tradeType == TradeType.SELL? HistoryStatus.지정가매도체결 : HistoryStatus.지정가매수체결;
            case MARKET:
                return tradeType == TradeType.SELL? HistoryStatus.시장가매도체결 : HistoryStatus.시장가매수체결;
            default:
                throw TradeException.of(TradeErrorCode.SERVER_ERROR);
        }
    }

    private int buyHokaToMarketPrice(int price) {
        if(price <= 2_000) {
            return price - 1;
        }
        if(price <= 5_000) {
            return price - 5;
        }
        if(price <= 20_000) {
            return price - 10;
        }
        if(price <= 50_000) {
            return price - 50;
        }
        if(price <= 200_000) {
            return price - 100;
        }
        return price - 1_000;
    }
}