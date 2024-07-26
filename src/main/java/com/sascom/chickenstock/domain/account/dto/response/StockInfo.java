package com.sascom.chickenstock.domain.account.dto.response;

public record StockInfo (
    String companyTitle,
    Integer price,
    Integer volume
) {
    
}
