package com.sascom.chickenstock.domain.auth.dto.request;

public record RequestLoginMember(
        String email,
        String password
){
}
