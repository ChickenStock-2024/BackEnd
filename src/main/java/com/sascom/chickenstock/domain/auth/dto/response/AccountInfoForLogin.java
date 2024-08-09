package com.sascom.chickenstock.domain.auth.dto.response;

import com.sascom.chickenstock.domain.account.entity.Account;

public record AccountInfoForLogin(
        Boolean isCompParticipant,
        Long balance,
        Integer rating
) {

    public static AccountInfoForLogin create(boolean isCompParticipant, Long balance, Integer rating) {
        if (isCompParticipant) {
            return new AccountInfoForLogin(isCompParticipant, balance, rating);
        } else {
            return new AccountInfoForLogin(false, 0L, 0);
        }
    }
}
