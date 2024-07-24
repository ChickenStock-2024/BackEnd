package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.request.AccountCreateRequest;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class AccountService {

    private AccountRepository accountRepository;
    private MemberRepository memberRepository;
    private CompetitionRepository competitionRepository;

    public Long createAccount(Long memberId, Long competitionId){

        // TODO: 커스텀 에러로 수정 필요
        Member member = memberRepository.findById(memberId)
                .orElseThrow(EntityNotFoundException::new);
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(EntityNotFoundException::new);
        Account account = new Account(
            member,
            competition
        );
        return accountRepository.save(account).getId();
    }
}
