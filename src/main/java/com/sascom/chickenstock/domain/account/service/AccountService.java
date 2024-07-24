package com.sascom.chickenstock.domain.account.service;

import com.sascom.chickenstock.domain.account.dto.request.AccountCreateRequest;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.member.entity.Member;
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

    public Long createAccount(AccountCreateRequest request){
        Member member = memberRepository.findById(request.memberId());
        Competition competition = competitionRepository.findById(request.competitionId());
        Account account = new Account(
            member,
            competition
        );
        return accountRepository.save(account).getId();
    }
}
