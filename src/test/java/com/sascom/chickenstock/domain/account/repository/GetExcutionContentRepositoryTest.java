package com.sascom.chickenstock.domain.account.repository;

import com.sascom.chickenstock.domain.account.dto.response.HistoryInfo;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.domain.history.entity.HistoryStatus;
import com.sascom.chickenstock.domain.history.repository.HistoryRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


// 체결내역 조회 테스트 코드

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GetExcutionContentRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private CompetitionRepository competitionRepository;

    private Company company;
    private Member member;
    private Account account;
    private History history1;
    private History history2;
    private Competition competition;

    @BeforeEach
    void setUp() {
        member = new Member("namesnames","97gkswn@gmail.com","1234");
        competition = new Competition(
                "1회대회",
                LocalDateTime.of(2023, 8, 15, 10, 30),
                LocalDateTime.of(2023, 8, 22, 10, 30)
        );
        account = new Account(member,competition);
        company = new Company("삼성전자","003288");
        history1 = new History(account,company,2000,20, HistoryStatus.지정가매수체결);
        history2 = new History(account,company,1000,10, HistoryStatus.지정가매도체결);

    }

    @Test
    void testFindById() {
        //given
        competitionRepository.save(competition);
        companyRepository.save(company);
        memberRepository.save(member);
        accountRepository.save(account);
        historyRepository.save(history1);
        historyRepository.save(history2);

        // when
        List<History> executionContent = historyRepository.findExecutionContent(account.getId());

        // then
        assertThat(executionContent).hasSize(2);
        assertThat(executionContent).extracting("status")
                .containsOnly(HistoryStatus.지정가매도체결, HistoryStatus.지정가매수체결);
    }
}
