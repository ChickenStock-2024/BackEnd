package com.sascom.chickenstock.domain.account.controller;

import com.sascom.chickenstock.domain.account.dto.request.AccountCreateRequest;
import com.sascom.chickenstock.domain.account.service.AccountService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountService accountService;

    public Long createAccount(@RequestBody AccountCreateRequest request) {
        Long accountId = accountService.createAccount(request);
        return accountId;
    }
}
