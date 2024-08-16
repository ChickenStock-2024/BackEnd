package com.sascom.chickenstock.domain.socket;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.beans.factory.InitializingBean;

@Component
@DependsOn("webSocketStompClient")
public class WebSocketClientInitializer implements InitializingBean {

    @Value("${websocket.url}")
    private String url;

    private final WebSocketStompClient stompClient;
    private final StompSessionHandler stompSessionHandler;

    @Autowired
    public WebSocketClientInitializer(WebSocketStompClient stompClient, MyStompSessionHandler stompSessionHandler) {
        this.stompClient = stompClient;
        this.stompSessionHandler = stompSessionHandler;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            StompSession session = stompClient.connectAsync(url, stompSessionHandler).get();
//            System.out.println("WebSocket connection initialized");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}