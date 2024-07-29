package com.sascom.chickenstock.domain.account.dto.response;

public record StockInfo (
    String companyName,
    Integer price,
    Integer volume
) {
    
}
