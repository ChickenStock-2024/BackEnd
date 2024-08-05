package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.response.StockInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;


    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, LocalDateTime endLocalDateTime) {
        Duration expireDuration = Duration.between(LocalDateTime.now(), endLocalDateTime);

        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, expireDuration.getSeconds(), TimeUnit.SECONDS);
    }

    @Transactional(readOnly = true)
    public Optional<String> getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();

        Object result = values.get(key);
        if (result == null) {
            return Optional.empty();
        }

        return Optional.of(result.toString());

    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void expireValues(String key, int timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.MILLISECONDS);
    }

    public void setHashOps(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    @Transactional(readOnly = true)
    public String getHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    }

    public void deleteHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }

//    public List<StockInfo> getStockInfo(Long accountId){
//
//    }

    public void setStockInfo(Long accountId, String companyName, Integer price, Integer volume) {
        String key = "account:" + accountId +":companyName:" + companyName;
        Map<String, String> stockData = new HashMap<>();
        stockData.put("price", price.toString());
        stockData.put("volume", volume.toString());
        setHashOps(key, stockData);
    }

    public Map<String, Map<String, String>> getStockInfo(Long accountId) {
        String pattern = "accountId:" + accountId + ":companyName:*";
        Set<String> keys = redisTemplate.keys(pattern);

        Map<String, Map<String, String>> StockInfo = new HashMap<>();

        if (keys != null && !keys.isEmpty()) {
            HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

            for (String key : keys) {
                Map<Object, Object> entries = hashOps.entries(key);
                Map<String, String> stockData = new HashMap<>();
                for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                    stockData.put(entry.getKey().toString(), entry.getValue().toString());
                }
                StockInfo.put(key, stockData);
            }
        }
        return StockInfo;
    }

    public void updateStockInfo(Long accountId, String companyName, int newAmount, double newPrice) {
        String pattern = "accountId:" + accountId + ":companyName:" + companyName;
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

            for (String key : keys) {
                // 기존의 값을 가져옵니다.
                Map<Object, Object> entries = hashOps.entries(key);

                // 필요한 필드만 삭제합니다.
                if (entries.containsKey("amount")) {
                    hashOps.delete(key, "amount");
                }
                if (entries.containsKey("price")) {
                    hashOps.delete(key, "price");
                }

                // 새로운 값을 추가합니다.
                hashOps.put(key, "amount", String.valueOf(newAmount));
                hashOps.put(key, "price", String.valueOf(newPrice));
            }
        }
    }


}