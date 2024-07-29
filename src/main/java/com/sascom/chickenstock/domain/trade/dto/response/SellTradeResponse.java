package com.sascom.chickenstock.domain.trade.dto.response;

import com.sascom.chickenstock.domain.trade.dto.request.SellTradeRequest;
import lombok.Builder;

@Builder
public record SellTradeResponse (
        String message,
        SellTradeRequest sellTradeRequest
) { }
