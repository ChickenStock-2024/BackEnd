package com.sascom.chickenstock.domain.account.dto.request;

public record AccountCreateRequest(
        Long memberId,
        Long competitionId
) { }
