package com.sascom.chickenstock.domain.company.controller;

import com.sascom.chickenstock.domain.company.dto.response.CompanyInfoResponse;
import com.sascom.chickenstock.domain.company.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stock")
public class CompanyController {

    private final CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping
    public ResponseEntity<List<CompanyInfoResponse>> getAllCompanyInfo() {
        List<CompanyInfoResponse> companyInfoList = companyService.getCompanyInfoList();
        return ResponseEntity.ok(companyInfoList);
    }

    @GetMapping
    @RequestMapping("/{stockId}")
    public ResponseEntity<CompanyInfoResponse> getCompanyById(@PathVariable("stockId") Long stockId) {
        CompanyInfoResponse companyInfo = companyService.getCompanyInfo(stockId);
        return ResponseEntity.ok(companyInfo);
    }

    @GetMapping
    @RequestMapping("/search")
    public ResponseEntity<List<CompanyInfoResponse>> getCompanyByName(@RequestParam("value") String stockName) {
        List<CompanyInfoResponse> companyInfoList = companyService.searchCompany(stockName);
        return ResponseEntity.ok(companyInfoList);
    }
}