package com.sascom.chickenstock.domain.competition.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.dto.request.CompetitionRequest;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionHistoryResponse;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionListResponse;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.error.code.CompetitionErrorCode;
import com.sascom.chickenstock.domain.competition.error.exception.CompetitionNotFoundException;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompetitionService {

    private CompetitionRepository competitionRepository;
    private AccountRepository accountRepository;

    @Autowired
    public CompetitionService(CompetitionRepository competitionRepository, AccountRepository accountRepository){
        this.competitionRepository = competitionRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void save(CompetitionRequest competitionRequest){
        Competition competition = new Competition(
                competitionRequest.title(),
                competitionRequest.startAt(),
                competitionRequest.endAt()
        );
        competitionRepository.save(competition);
    }

    public List<CompetitionListResponse> findAllCompetitionByMember(Long memberId) {
        List<Account> accountList = accountRepository.findByMemberId(memberId);

        List<CompetitionListResponse> competitionListResponses = new ArrayList<>();
        for (Account account : accountList) {
            Competition competition = account.getCompetition();
            competitionListResponses.add(CompetitionListResponse.builder()
                    .competitionId(competition.getId())
                    .title(competition.getTitle())
                    .startAt(competition.getStartAt())
                    .endAt(competition.getEndAt())
                    .rank(account.getRanking())
                    .ratingChange(account.getRatingChange())
                    .balance(account.getBalance())
                    .build()
            );
        }

        return competitionListResponses;
    }

    public List<CompetitionHistoryResponse> findAllHistoryByCompetition(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        return account.getHistories().stream()
                .map(history ->
                        CompetitionHistoryResponse.builder()
                                .companyName(history.getCompany().getName())
                                .price(history.getPrice())
                                .quantity(history.getVolume())
                                .status(history.getStatus())
                                .createdAt(history.getCreatedAt())
                                .build())
                .toList();
    }

}
