package com.sascom.chickenstock.domain.account.controller;

import com.sascom.chickenstock.domain.account.dto.request.AccountCreateRequest;
import com.sascom.chickenstock.domain.account.dto.request.StockOrderRequest;
import com.sascom.chickenstock.domain.account.dto.response.AccountInfoResponse;
import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.trade.dto.response.BuyTradeResponse;
import com.sascom.chickenstock.domain.trade.dto.response.SellTradeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    private AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Long createAccount(@RequestBody AccountCreateRequest request) {
        Long accountId = accountService.createAccount(request.memberId(), request.competitionId());
        return accountId;
    }

    @PostMapping("/{accountId}")
    public AccountInfoResponse accountInfoResponse(@PathVariable("accountId") Long accountId){
        return accountService.getAccountInfo(accountId);
    }

    @PostMapping("/buy")
    public ResponseEntity<BuyTradeResponse> buyStocks(@RequestBody StockOrderRequest stockOrderRequest) {
        BuyTradeResponse response = accountService.buyStocks(stockOrderRequest);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/sell")
    public ResponseEntity<SellTradeResponse> sellStocks(@RequestBody StockOrderRequest stockOrderRequest) {
        SellTradeResponse response = accountService.sellStocks(stockOrderRequest);

        return ResponseEntity.ok().body(response);
    }


}
