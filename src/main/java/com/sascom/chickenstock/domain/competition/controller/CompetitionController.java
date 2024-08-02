package com.sascom.chickenstock.domain.competition.controller;

import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.competition.dto.request.CompetitionParticipationRequest;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionListResponse;
import com.sascom.chickenstock.domain.competition.service.CompetitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/competition")
public class CompetitionController {

    private final CompetitionService competitionService;
    private final AccountService accountService;

    @PostMapping
    public void participateCompetition(@RequestBody CompetitionParticipationRequest request){
        accountService.createAccount(request.memberId(), request.competitionId());
    }

    @GetMapping("/all/{memberId}")
    public ResponseEntity<List<CompetitionListResponse>> getAllCompetition(@PathVariable("memberId") Long memberId){
        List<CompetitionListResponse> competitionListResponses = competitionService.findAllCompetitionByMember(memberId);

        return ResponseEntity.ok().body(competitionListResponses);
    }
}
