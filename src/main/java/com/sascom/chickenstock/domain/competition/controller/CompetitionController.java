package com.sascom.chickenstock.domain.competition.controller;

import com.sascom.chickenstock.domain.competition.dto.request.CompetitionParticipationRequest;
import com.sascom.chickenstock.domain.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    private CompetitionService competitionService;
    private AccountService accountService;

    @PostMapping
    public void participateCompetition(CompetitionParticipationRequest request){
        accountService.createAccount(request);
    }
}
