package com.sascom.chickenstock.domain.companylike.dto.response;

import lombok.Builder;

public record CompanyLikeResponse(
        String companyName,
        String memberName,
        Long companyId,
        String message
) {
    @Builder
    public CompanyLikeResponse(String companyName, String memberName, Long companyId, String message) {
        this.companyName = companyName;
        this.memberName = memberName;
        this.companyId = companyId;
        this.message = message;
    }
}
