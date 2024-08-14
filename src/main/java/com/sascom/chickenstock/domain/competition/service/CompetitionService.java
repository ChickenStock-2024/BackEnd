package com.sascom.chickenstock.domain.competition.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.competition.dto.request.CompetitionRequest;
import com.sascom.chickenstock.domain.competition.dto.response.ActiveCompetitionResponse;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionHistoryResponse;
import com.sascom.chickenstock.domain.competition.dto.response.CompetitionListResponse;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                    .accountId(account.getId())
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

    public ActiveCompetitionResponse findLatestCompetition() {
        Optional<Competition> competition = competitionRepository.findTopByOrderByIdDesc();

        if (competition.isPresent() && isActiveCompetition(competition.get())) {
            Competition activeCompetition = competition.get();
            return new ActiveCompetitionResponse(
                    true,
                    activeCompetition.getTitle(),
                    activeCompetition.getStartAt(),
                    activeCompetition.getEndAt()
            );
        }

        return new ActiveCompetitionResponse(false, null, null, null);
    }

    public boolean isActiveCompetition(Competition competition) {
        LocalDateTime now = LocalDateTime.now();
        return competition.getStartAt().isBefore(now) && competition.getEndAt().isAfter(now);
    }

}
