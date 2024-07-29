package com.sascom.chickenstock.domain.account.dto.response;

import java.util.List;

// 체결 내역
public record ExecutionContentResponse(
        List<HistoryInfo> execution
) {
}
