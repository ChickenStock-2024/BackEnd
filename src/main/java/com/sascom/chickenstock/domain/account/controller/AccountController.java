package com.sascom.chickenstock.domain.account.controller;

import com.sascom.chickenstock.domain.account.dto.request.AccountCreateRequest;
import com.sascom.chickenstock.domain.account.dto.request.CancelOrderRequest;
import com.sascom.chickenstock.domain.account.dto.request.StockOrderRequest;
import com.sascom.chickenstock.domain.account.dto.response.AccountInfoResponse;
import com.sascom.chickenstock.domain.account.dto.response.ExecutionContentResponse;
import com.sascom.chickenstock.domain.account.dto.response.UnexecutionContentResponse;
import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.trade.dto.response.CancelOrderResponse;
import com.sascom.chickenstock.domain.trade.dto.response.TradeResponse;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{accountId}")
    public AccountInfoResponse getAccountInfo(@PathVariable("accountId") Long accountId){
        return accountService.getAccountInfo(accountId);
    }


    @GetMapping("/{accountId}/execution")
    public ExecutionContentResponse getExecutionContent(@PathVariable("accountId") Long accountId){
        return accountService.getExecutionContent(accountId);
    }

    @GetMapping("/{accountId}/unexecution")
    public UnexecutionContentResponse getUnexecutionContent(@PathVariable("accountId") Long accountId){
        return accountService.getUnexecutionContent(accountId);
    }

    @PostMapping("/buy/limit")
    public ResponseEntity<TradeResponse> buyLimitStocks(@RequestBody StockOrderRequest stockOrderRequest) {
        TradeResponse response = accountService.buyLimitStocks(stockOrderRequest);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/sell/limit")
    public ResponseEntity<TradeResponse> sellLimitStocks(@RequestBody StockOrderRequest stockOrderRequest) {
        TradeResponse response = accountService.sellLimitStocks(stockOrderRequest);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/buy/market")
    public ResponseEntity<TradeResponse> buyMarketStocks(@RequestBody StockOrderRequest stockOrderRequest) {
        TradeResponse response = accountService.buyMarketStocks(stockOrderRequest);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/sell/market")
    public ResponseEntity<TradeResponse> sellMarketStocks(@RequestBody StockOrderRequest stockOrderRequest) {
        TradeResponse response = accountService.sellMarketStocks(stockOrderRequest);

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<CancelOrderResponse> cancelOrder(@RequestBody CancelOrderRequest cancelOrderRequest) {
        CancelOrderResponse response = accountService.cancelStockOrder(cancelOrderRequest);

        return ResponseEntity.ok().body(response);
    }
}
