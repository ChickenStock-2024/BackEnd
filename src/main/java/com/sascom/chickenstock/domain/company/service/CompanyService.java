package com.sascom.chickenstock.domain.company.service;

import com.sascom.chickenstock.domain.company.dto.response.CompanyInfoResponse;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Autowired
    CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyInfoResponse getCompanyInfo(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));
        return CompanyInfoResponse.builder()
                .id(company.getId())
                .code(company.getCode())
                .name(company.getName())
                .status(company.getStatus())
                .build();
    }

    public List<CompanyInfoResponse> searchCompany(String stockName) {
        List<Company> companyList = companyRepository.findByNameContains(stockName);
        return collectToCompanyInfoResponse(companyList);
    }

    public List<CompanyInfoResponse> getCompanyInfoList() {
        List<Company> companyList = companyRepository.findAll();
        return collectToCompanyInfoResponse(companyList);
    }

    private List<CompanyInfoResponse> collectToCompanyInfoResponse(List<Company> companyList) {
        return companyList.stream().map(company ->
                        CompanyInfoResponse.builder()
                                .id(company.getId()).code(company.getCode())
                                .name(company.getName()).status(company.getStatus()).build())
                .toList();
    }

    public Company findById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() ->  CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));
    }
}
