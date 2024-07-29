package com.sascom.chickenstock.domain.account.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record SellStockRequest (
        Long accountId,
        Long memberId,
        Long companyId,
        Long competitionId,
        String companyName,
        Integer unitCost,
        Integer amount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime orderTime
){
}
