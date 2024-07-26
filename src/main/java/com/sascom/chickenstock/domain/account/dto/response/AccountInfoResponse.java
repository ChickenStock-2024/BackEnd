package com.sascom.chickenstock.domain.account.dto.response;

import java.util.List;

public record AccountInfoResponse(
        Long balance,
        List<StockInfo> stocks
) {
}
