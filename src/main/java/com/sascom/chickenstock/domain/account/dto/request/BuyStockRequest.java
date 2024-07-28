package com.sascom.chickenstock.domain.account.dto.request;

import java.time.LocalDateTime;

public record BuyStockRequest (
        Long accountId,
        Long memberId,
        Long companyId,
        Long competitionId,
        String companyName,
        Integer unitCost,
        Integer amount,
        LocalDateTime orderTime
){
}
