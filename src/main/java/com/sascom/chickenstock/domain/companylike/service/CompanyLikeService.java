package com.sascom.chickenstock.domain.companylike.service;

import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.companylike.dto.response.CompanyLikeResponse;
import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.companylike.repository.CompanyLikeRepository;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
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

    public CompanyLikeResponse makeLikeRelationship(Long companyId, Long memberId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        CompanyLike companyLike = new CompanyLike(member, company);
        companyLikeRepository.save(companyLike);

        return CompanyLikeResponse.builder()
                .companyId(company.getId())
                .companyName(company.getName())
                .memberName(member.getNickname())
                .message("좋아요 추가")
                .build();
    }

    public CompanyLikeResponse removeLikeRelationship(Long companyId, Long memberId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> CompanyNotFoundException.of(CompanyErrorCode.NOT_FOUND));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

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
