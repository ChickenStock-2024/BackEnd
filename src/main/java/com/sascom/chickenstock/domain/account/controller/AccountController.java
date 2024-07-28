package com.sascom.chickenstock.domain.account.controller;

import com.sascom.chickenstock.domain.account.dto.request.AccountCreateRequest;
import com.sascom.chickenstock.domain.account.dto.request.BuyStockRequest;
import com.sascom.chickenstock.domain.account.dto.response.AccountInfoResponse;
import com.sascom.chickenstock.domain.account.dto.response.StockInfo;
import com.sascom.chickenstock.domain.account.service.AccountService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> buyStocks(@RequestBody BuyStockRequest buyStockRequest) {
        accountService.buyStocks(buyStockRequest);

        return ResponseEntity.ok().body("구매요청 성공");
    }

    @PostMapping("/sell")
    public ResponseEntity<?> sellStocks(@RequestBody BuyStockRequest sellStockRequest) {
        accountService.sellStocks(sellStockRequest);

        return ResponseEntity.ok().body("판매요청 성공");
    }


}
