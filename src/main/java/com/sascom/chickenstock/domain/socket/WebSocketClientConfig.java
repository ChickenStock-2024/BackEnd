package com.sascom.chickenstock.domain.socket;


import com.sascom.chickenstock.domain.trade.dto.RealStockTradeDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
public class WebSocketClientConfig {

    @Bean
    public WebSocketStompClient webSocketStompClient() {
        WebSocketClient client = new StandardWebSocketClient();
        WebSocketStompClient stompClient = new WebSocketStompClient(client);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompClient.setMessageConverter(new StringMessageConverter());

//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.afterPropertiesSet();
//        stompClient.setTaskScheduler(taskScheduler);

        return stompClient;
    }

}
