package com.sascom.chickenstock.domain.socket;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sascom.chickenstock.domain.trade.dto.RealStockTradeDto;
import com.sascom.chickenstock.domain.trade.dto.TradeType;
import com.sascom.chickenstock.domain.trade.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.logging.Logger;

@Component
public class MyStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger logger = Logger.getLogger(MyStompSessionHandler.class.getName());
    private final ObjectMapper objectMapper;
    private final TradeService tradeService;

    @Autowired
    public MyStompSessionHandler(TradeService tradeService) {
        objectMapper = new ObjectMapper();
        this.tradeService = tradeService;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        session.subscribe("/stock-purchase", this);
        System.out.println("Connected and subscribed to /stock-purchase");
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
                jsonNode.get("stockCode").asText(),
                jsonNode.get("currentPrice").asInt(),
                jsonNode.get("transactionVolume").asInt(),
                switch(jsonNode.get("transactionType").asInt()) {
                    case 1 -> TradeType.BUY;
                    case 5 -> TradeType.SELL;
                    default -> throw new IllegalStateException("parseError");
                });
    }
}