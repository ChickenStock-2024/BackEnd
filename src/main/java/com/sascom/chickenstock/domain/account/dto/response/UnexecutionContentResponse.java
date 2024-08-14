package com.sascom.chickenstock.domain.account.dto.response;

import com.sascom.chickenstock.domain.trade.dto.OrderType;

import java.util.List;

// 미체결 내역
public record UnexecutionContentResponse(
        List<UnexcutedStockInfo> unexcutedStockInfos
) {
}
