package com.sascom.chickenstock.domain.competition.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.competition.dto.request.CompetitionRequest;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CompetitionService {

    private CompetitionRepository competitionRepository;

    @Autowired
    public CompetitionService(CompetitionRepository competitionRepository){
        this.competitionRepository = competitionRepository;
    }

    @Transactional
    public void save(CompetitionRequest competitionRequest){
        // 대회 생성이니까 계좌가 아직 없을 거니까 account는 빈 리스트로
        List<Account> accounts = new ArrayList<>();
        Competition competition = new Competition(
                competitionRequest.getTitle(),
                competitionRequest.getStartAt(),
                competitionRequest.getEndAt(),
                accounts
        );
        competitionRepository.save(competition);
    }



}
