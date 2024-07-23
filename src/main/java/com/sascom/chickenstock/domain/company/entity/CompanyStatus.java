package com.sascom.chickenstock.domain.company.entity;

public enum CompanyStatus {
    LISTED,
    UNLISTED;

    public String temp() {
        return CompanyStatus.LISTED.name();
    }
}
