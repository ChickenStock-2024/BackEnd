//package com.sascom.chickenstock.domain.socket;
//
//
//import org.springframework.messaging.simp.stomp.*;
//import org.springframework.stereotype.Component;
//
//import java.lang.reflect.Type;
//import java.util.logging.Logger;
//
//@Component
//public class MyStompSessionHandler extends StompSessionHandlerAdapter {
//
//    private static final Logger logger = Logger.getLogger(MyStompSessionHandler.class.getName());
//
//    @Override
//    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//        session.subscribe("/stock-purchase", this);
//        System.out.println("Connected and subscribed to /stock-hoka");
//    }
//
//    @Override
//    public void handleFrame(StompHeaders headers, Object payload) {
//        System.out.println("Received: " + payload.toString());
//        // Handle the received payload here
//    }
//
//    @Override
//    public Type getPayloadType(StompHeaders headers) {
//        return String.class;
//    }
//
//    @Override
//    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
//        logger.severe("Failure in WebSocket handling: " + exception.getMessage());
//        throw new RuntimeException("Failure in WebSocket handling", exception);
//    }
//}