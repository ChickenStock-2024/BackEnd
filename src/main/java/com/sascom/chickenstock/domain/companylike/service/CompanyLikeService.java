package com.sascom.chickenstock.domain.companylike.service;

import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.companylike.dto.response.CompanyLikeResponse;
import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.companylike.repository.CompanyLikeRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyLikeService {

    private final CompanyLikeRepository companyLikeRepository;
    private final CompanyRepository companyRepository;
    private final MemberRepository memberRepository;

    @Autowired
    CompanyLikeService(CompanyLikeRepository companyLikeRepository, CompanyRepository companyRepository, MemberRepository memberRepository) {
        this.companyLikeRepository = companyLikeRepository;
        this.companyRepository = companyRepository;
        this.memberRepository = memberRepository;
    }

    public CompanyLikeResponse makeLikeRelationship(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("invalid companyId"));
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("invalid userId"));

        CompanyLike companyLike = new CompanyLike(member, company);
        companyLikeRepository.save(companyLike);

        return CompanyLikeResponse.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .memberName(member.getNickname())
                .message("좋아요 추가")
                .build();
    }

    public CompanyLikeResponse removeLikeRelationship(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalStateException("invalid companyId"));
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new IllegalStateException("invalid userId"));

        CompanyLike companyLike = companyLikeRepository.findByCompanyIdAndMemberId(companyId, 1L);
        companyLikeRepository.delete(companyLike);

        return CompanyLikeResponse.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .memberName(member.getNickname())
                .message("좋아요 삭제")
                .build();
    }

}
