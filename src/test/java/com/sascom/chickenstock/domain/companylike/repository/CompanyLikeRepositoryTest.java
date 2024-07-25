package com.sascom.chickenstock.domain.companylike.repository;

import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CompanyLikeRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CompanyLikeRepository companyLikeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Company company;
    private Member member;
    private CompanyLike companyLike;

    @BeforeEach
    void setUp() {
        member = new Member("nickname", "ddd@sss.com", "password");
        company = new Company("test company", "123456");
        companyLike = new CompanyLike(member, company);
    }

    @Test
    void testFindById() {
        //given
        companyRepository.save(company);
        memberRepository.save(member);
        companyLikeRepository.save(companyLike);

        // when
        CompanyLike companyLike1 = companyLikeRepository.findByCompanyIdAndMemberId(company.getId(), member.getId());

        // then
        Assertions.assertThat(companyLike1.getCompany().getId()).isEqualTo(company.getId());
        Assertions.assertThat(companyLike1.getMember().getId()).isEqualTo(member.getId());
    }
}
