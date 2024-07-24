package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.member.dto.request.ChangeInfoRequest;
import com.sascom.chickenstock.domain.member.dto.response.ChangeInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
//    private final AccountRepository accountRepository;
//    private final CompetitionRepository competitionRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository/*,
                         AccountRepository accountRepository,
                         CompetitionRepository competitionRepository */) {
        this.memberRepository = memberRepository;
//        this.accountRepository = accountRepository;
//        this.competitionRepository = competitionRepository;
    }

    public MemberInfoResponse lookUpMemberInfo(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("invalid userId"));

        return toMemberInfoResponse(member);
    }

    @Transactional
    public ChangeInfoResponse changeMemberInfo(
            Long userId,
            ChangeInfoRequest changeInfoRequest) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("invalid userId"));
        // hash given password or use spring security

        if(!changeInfoRequest.getOldPassword().equals(member.getPassword())) {
            throw new IllegalStateException("incorrect Old Password");
        }
        if(changeInfoRequest.getNewPassword() == null ||
                !changeInfoRequest.getNewPassword().equals(
                changeInfoRequest.getNewPasswordCheck())) {
            throw new IllegalStateException("New Password and New Password Check are not equal");
        }
        if(!isSafePassword(changeInfoRequest.getNewPassword())){
            throw new IllegalStateException("New Password is not safe.");
        }

        // hash new password, also. need to edit below code.
        String hashedNewPassword = changeInfoRequest.getNewPassword();
        member.updatePassword(hashedNewPassword);
        Member savedMember = memberRepository.save(member);
        return new ChangeInfoResponse(savedMember.getNickname());
    }

    public PrefixNicknameInfosResponse searchPrefixNicknameMemberInfos(String prefix) {
        List<Member> memberList = memberRepository.findFirst10ByNicknameStartingWithOrderByNickname(prefix);

        List<MemberInfoResponse> result = new ArrayList<>(memberList.size());
        memberList.forEach((member) -> result.add(toMemberInfoResponse(member)));

        return PrefixNicknameInfosResponse.builder()
                .memberList(result)
                .build();
    }

    // Member -> MemberInfoResponse
    private MemberInfoResponse toMemberInfoResponse(Member member) {
        // find latest competition and latest rating from account.
        // this implementation may occur 1 + N problem.
        int ratestRating = 1500;
        LocalDateTime latestDate = LocalDateTime.MIN;
        for(Account account : member.getAccounts()){
            LocalDateTime competitionEndAt = account.getCompetition().getEndAt();
            if(competitionEndAt.isAfter(latestDate)){
                latestDate = competitionEndAt;
                ratestRating = account.getRating().getLatestRating();
            }
        }

        return MemberInfoResponse.builder()
                .memberId(member.getId())
                .point(member.getPoint())
                .nickname(member.getNickname())
                .rating(ratestRating)
                .build();
    }

    // check that given new password is fit to safe password standard.
    // eg) contains at least 3 alphabets, at least one of special symbols as !, @, #, $, ... .
    private boolean isSafePassword(String password) {
        // TODO: implementation
        return true;
    }
}
