package com.sascom.chickenstock.domain.trade.dto.response;

import com.sascom.chickenstock.domain.account.dto.request.BuyStockRequest;
import com.sascom.chickenstock.domain.trade.dto.request.BuyTradeRequest;
import lombok.Builder;

@Builder
public record BuyTradeResponse(
        String message,
        BuyTradeRequest buyTradeRequest
) { }
