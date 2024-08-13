package com.sascom.chickenstock.domain.dailystockprice.dto.request.fis;

import lombok.Builder;

@Builder
public record AccessTokenReqeust (
        String grant_type,
        String appkey,
        String appsecret
) {
}
