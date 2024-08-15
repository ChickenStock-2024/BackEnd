package com.sascom.chickenstock.global.kafkaproducer.dto;

public record NotificationMessageDto(
        String receiver,
        String title,
        String body
) {
}
