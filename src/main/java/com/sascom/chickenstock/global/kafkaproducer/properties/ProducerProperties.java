package com.sascom.chickenstock.global.kafkaproducer.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@ConfigurationProperties(prefix = "kafka.producer")
public record ProducerProperties(
        String bootstrapServers,
        String keySerializer,
        String valueSerializer,
        Map<String, String> topic
) {
}
