package com.sascom.chickenstock.global.kafkaproducer;

import com.sascom.chickenstock.global.kafkaproducer.dto.NotificationMessageDto;
import com.sascom.chickenstock.global.kafkaproducer.properties.ProducerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

// KafkaProducer
/* 바라보고 있는 kafka broker topic 메세지 발행 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProducer {

    private final ProducerProperties producerProperties;
    private final KafkaTemplate<String, NotificationMessageDto> kafkaTemplate;

    public void sendEmailMessageToKafka(NotificationMessageDto message) {
        CompletableFuture<SendResult<String, NotificationMessageDto>> future = this.kafkaTemplate.send(producerProperties.topic().get("email"), message);

        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                log.info("Producer Send Email Message: {}, offset: {}", result.getProducerRecord().value(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message: {}", message, throwable);
            }
        });
    }

    public void sendAlarmMessageToKafka(NotificationMessageDto message) {
        CompletableFuture<SendResult<String, NotificationMessageDto>> future = this.kafkaTemplate.send(producerProperties.topic().get("fcm"), message);

        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                log.info("Producer Send FCM: {}, offset: {}", result.getProducerRecord().value(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message: {}", message, throwable);
            }
        });
    }
}