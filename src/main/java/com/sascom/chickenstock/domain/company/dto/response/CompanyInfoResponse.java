package com.sascom.chickenstock.domain.company.dto.response;

import com.sascom.chickenstock.domain.company.entity.CompanyStatus;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyException;
import lombok.Builder;

public record CompanyInfoResponse(
        Long id,
        String name,
        String code,
        CompanyStatus status
) {
    @Builder
    public CompanyInfoResponse {
        if (id <= 0L) {
            throw CompanyException.of(CompanyErrorCode.INVALID_VALUE);
        }
        if (name == null || name.isBlank()) {
            throw CompanyException.of(CompanyErrorCode.INVALID_VALUE);
        }
    }
}
