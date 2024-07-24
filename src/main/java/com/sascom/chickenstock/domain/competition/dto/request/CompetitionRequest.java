package com.sascom.chickenstock.domain.competition.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class CompetitionRequest {
    private String title;
    private Date startAt;
    private Date endAt;
}
