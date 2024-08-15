package com.sascom.chickenstock.global.kafkaproducer;

import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;
import com.sascom.chickenstock.domain.member.service.MemberService;
import com.sascom.chickenstock.global.kafkaproducer.dto.NotificationMessageDto;
import com.sascom.chickenstock.global.kafkaproducer.properties.ProducerProperties;
import com.sascom.chickenstock.global.util.SecurityUtil;
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
    private final MemberService memberService;

    public void sendEmailMessageToKafka(NotificationMessageDto message) {
        String topic = producerProperties.topic().get("email");
        sendMessage(topic, message, "Producer Send Email Message: {}, offset: {}");
    }

    public void sendAlarmMessageToKafka(NotificationMessageDto message) {
        String topic = producerProperties.topic().get("fcm");
        sendMessage(topic, message, "Producer Send FCM: {}, offset: {}");
    }

    private void sendMessage(String topic, NotificationMessageDto message, String s) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        boolean isAvailable = checkMemberNotificationPermission(memberId, topic);
        if (!isAvailable) {
            log.info("member: {}, topic: {} 알림 비동의", memberId, topic);
            return;
        }

        CompletableFuture<SendResult<String, NotificationMessageDto>> future = this.kafkaTemplate.send(topic, message);

        future.whenComplete((result, throwable) -> {
            if (throwable == null) {
                log.info(s, result.getProducerRecord().value(), result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message: {}", message, throwable);
            }
        });
    }

    private boolean checkMemberNotificationPermission(Long memberId, String topic) {

        MemberInfoForLogin memberInfo = memberService.lookUpMemberInfoForLogin(memberId);

        if (topic.equals(producerProperties.topic().get("email"))) {
            return memberInfo.kakaotalkNoti();
        }
        if (topic.equals(producerProperties.topic().get("fcm"))) {
            return memberInfo.webNoti();
        }

        throw new IllegalArgumentException("잘못된 topic을 확인했습니다.");
    }
}