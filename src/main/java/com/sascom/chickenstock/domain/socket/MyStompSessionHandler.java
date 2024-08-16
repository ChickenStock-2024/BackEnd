package com.sascom.chickenstock.domain.socket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.trade.dto.RealStockTradeDto;
import com.sascom.chickenstock.domain.trade.dto.TradeType;
import com.sascom.chickenstock.domain.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger logger = Logger.getLogger(MyStompSessionHandler.class.getName());
    private final ObjectMapper objectMapper;
    private final TradeService tradeService;
    private final CompanyRepository companyRepository;

    @Autowired
    public MyStompSessionHandler(TradeService tradeService, CompanyRepository companyRepository) {
        objectMapper = new ObjectMapper();
        this.tradeService = tradeService;
        this.companyRepository = companyRepository;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        final String[] companyStockCodeList = {"005930", "009150", "000660", "299660", "042700",
                "035420", "035720", "028300", "084650", "257720"};
        for(String stockCode : companyStockCodeList) {
            session.subscribe("/stock-purchase/" + stockCode, this);
            logger.log(Level.INFO, "Connected and subscribed to /stock-purchase/{}", stockCode);
        }
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
//        System.out.println("Received: " + payload.toString());
        // Handle the received payload here
        try {
            RealStockTradeDto realStockTradeDto = parseMessage(payload.toString());
            tradeService.processRealStockTrade(realStockTradeDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.severe("Failure in WebSocket handling: " + exception.getMessage());
        throw new RuntimeException("Failure in WebSocket handling", exception);
    }

    private RealStockTradeDto parseMessage(String message) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(message);
        return new RealStockTradeDto(
                companyRepository.findByCode(jsonNode.get("stockCode").asText())
                        .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND))
                        .getId(),
                jsonNode.get("currentPrice").asInt(),
                jsonNode.get("transactionVolume").asInt(),
                switch(jsonNode.get("transactionType").asInt()) {
                    case 1 -> TradeType.BUY;
                    case 5 -> TradeType.SELL;
                    default -> throw new IllegalStateException("parseError");
                });
    }
}