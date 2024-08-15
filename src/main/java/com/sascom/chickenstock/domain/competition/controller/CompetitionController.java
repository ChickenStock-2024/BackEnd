package com.sascom.chickenstock.domain.competition.controller;

import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.competition.dto.request.CompetitionParticipationRequest;
import com.sascom.chickenstock.domain.competition.dto.request.CompetitionRegistRequest;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionInfoResponse;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionHistoryResponse;
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
    public ResponseEntity<Long> participateCompetition(@RequestBody CompetitionParticipationRequest request) {
        Long accountId = accountService.createAccount(request.memberId(), request.competitionId());
        return ResponseEntity.ok(accountId);
    }

    @GetMapping
    public ResponseEntity<CompetitionInfoResponse> getCompetition() {
        CompetitionInfoResponse latestCompetition = competitionService.findActiveCompetition();
        return ResponseEntity.ok(latestCompetition);
    }

    @GetMapping("/all/{memberId}")
    public ResponseEntity<List<CompetitionListResponse>> getAllCompetition(@PathVariable("memberId") Long memberId) {
        List<CompetitionListResponse> competitionListResponses = competitionService.findAllCompetitionByMember(memberId);

        return ResponseEntity.ok().body(competitionListResponses);
    }

    @GetMapping("/history/{account_id}")
    public ResponseEntity<List<CompetitionHistoryResponse>> getAllHistoryByCompetitionId(@PathVariable("account_id") Long accountId) {
        List<CompetitionHistoryResponse> competitionHistoryResponseList = competitionService.findAllHistoryByCompetition(accountId);

        return ResponseEntity.ok().body(competitionHistoryResponseList);
    }

    @PostMapping("/new")
    public ResponseEntity<CompetitionInfoResponse> registerCompetition(@RequestBody CompetitionRegistRequest competitionRegistRequest) {
        CompetitionInfoResponse competitionInfoResponse = competitionService.addCompetition(
                competitionRegistRequest.title(),
                competitionRegistRequest.startDate(),
                competitionRegistRequest.endDate(),
                competitionRegistRequest.adminKey()
        );

        return ResponseEntity.ok(competitionInfoResponse);
    }
}
