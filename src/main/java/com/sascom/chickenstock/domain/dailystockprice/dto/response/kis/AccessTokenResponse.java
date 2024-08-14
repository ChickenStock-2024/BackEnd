package com.sascom.chickenstock.domain.dailystockprice.dto.response.kis;

public record AccessTokenResponse(
        String access_token,
        String access_token_token_expired,
        String token_type,
        String expires_in
) {
}
