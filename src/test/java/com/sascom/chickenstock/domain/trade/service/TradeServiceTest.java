package com.sascom.chickenstock.domain.trade.service;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class TradeServiceTest {
    private TradeService tradeService;

    @BeforeEach
    void setUp() {
        tradeService.clear();
    }
}