package com.sascom.chickenstock.domain.trade.dto;

public enum MatchStatus {
    // 시장가와 맞지 않아서 취소
    CANCELED_BY_LOGIC,
    // 잔고 부족으로 취소
    CANCELED_BY_BALANCE,
    // 체결
    EXECUTED
}
