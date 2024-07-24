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
        Competition competition = new Competition(
                competitionRequest.title(),
                competitionRequest.startAt(),
                competitionRequest.endAt()
        );
        competitionRepository.save(competition);
    }



}
