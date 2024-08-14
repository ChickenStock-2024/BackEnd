package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.response.StockInfo;
import com.sascom.chickenstock.domain.account.dto.response.UnexecutionContentResponse;
import com.sascom.chickenstock.domain.trade.dto.TradeType;
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

//    public void setStockInfo(Long accountId, Long companyId, Integer price, Integer volume) {
//        String key = "accountId:" + accountId +":companyId:" + companyId;
//        Map<String, String> stockData = new HashMap<>();
//        stockData.put("price", price.toString());
//        stockData.put("volume", volume.toString());
//        setHashOps(key, stockData);
//    }

    public Map<String, Map<String, String>> getStockInfo(Long accountId) {
        String pattern = "accountId:" + accountId + ":companyId:*";
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

//    // 보유 주식(Redis)에 update(== set)
//    public void updateStockInfo(Long accountId, Long companyId, int newVolume, int newPrice) {
//        String pattern = "accountId:" + accountId + ":companyId:" + companyId;
//        Set<String> keys = redisTemplate.keys(pattern);
//
//        if (keys != null && !keys.isEmpty()) {
//            HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
//
//            for (String key : keys) {
//                // 기존의 값을 가져옵니다.
//                Map<Object, Object> entries = hashOps.entries(key);
//
//                // 필요한 필드만 삭제합니다.
//                if (entries.containsKey("volume")) {
//                    hashOps.delete(key, "volume");
//                }
//                if (entries.containsKey("price")) {
//                    hashOps.delete(key, "price");
//                }
//
//                // 새로운 값을 추가합니다.
//                hashOps.put(key, "volume", String.valueOf(newVolume));
//                hashOps.put(key, "price", String.valueOf(newPrice));
//            }
//        }
//    }

    public void updateStockInfo(Long accountId, Long companyId, int changeVolume, int changePrice) {
        String pattern = "accountId:" + accountId + ":companyId:" + companyId;
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

            for (String key : keys) {
                // 기존의 값을 가져옵니다.
                Map<Object, Object> entries = hashOps.entries(key);

                int updateVolume = Integer.valueOf(entries.get("volume").toString()) + changeVolume;
                int updatePrice = Integer.valueOf(entries.get("price").toString()) + changePrice;

                // 필요한 필드만 삭제합니다.
                if (entries.containsKey("volume")) {
                    hashOps.delete(key, "volume");
                }
                if (entries.containsKey("price")) {
                    hashOps.delete(key, "price");
                }

                // 새로운 값을 추가합니다.
                hashOps.put(key, "volume", String.valueOf(updateVolume));
                hashOps.put(key, "price", String.valueOf(updatePrice));
            }
        }
    }

    // 미체결내역 redis에 저장 (historyId + accountId)를 key로 해서
    public void setUnexecution(Long historyId, Long accountId, Long companyId,
                                  TradeType tradeType, int volume, int price) {
        // 정확한 키를 사용하여 접근
        String key = "historyId:" + historyId + ":accountId:" + accountId;
        HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

        // Redis에 해당 키가 존재하는지 체크 후 값 추가
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            hashOps.put(key, "companyId", String.valueOf(companyId));
            hashOps.put(key, "tradeType", String.valueOf(tradeType));
            hashOps.put(key, "volume", String.valueOf(volume));
            hashOps.put(key, "price", String.valueOf(price));
        } else {
            // 키가 없을 때의 처리 (필요시)
        }
    }

    // 매매 취소 -> 미체결내역 취소
    public void deleteUnexecution(Long historyId, Long accountId) {
        String pattern = "historyId:" + historyId + ":accountId:" + accountId;
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null && !keys.isEmpty()) {
            // 첫 번째 키만 사용
            String key = keys.iterator().next();
            HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();

            hashOps.delete(key, "tradeType");
            hashOps.delete(key, "volume");
            hashOps.delete(key, "price");
        }
    }

    // 해당 사용자의 미체결내역 모두 보여주기 (by memberId)
    public Map<String, Map<String, String>> getUnexcutionContent(Long accountId) {
        String pattern = "historyId:*" + ":accountId:" + accountId;
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

}