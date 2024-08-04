package com.sascom.chickenstock.domain.rival.service;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.rival.entity.Rival;
import com.sascom.chickenstock.domain.rival.error.code.RivalErrorCode;
import com.sascom.chickenstock.domain.rival.error.exception.ExistRivalException;
import com.sascom.chickenstock.domain.rival.error.exception.NotExistRivalException;
import com.sascom.chickenstock.domain.rival.error.exception.SameMemberException;
import com.sascom.chickenstock.domain.rival.repository.RivalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RivalService {

    private final RivalRepository rivalRepository;
    private final MemberRepository memberRepository;

    @Autowired
    private RivalService(RivalRepository rivalRepository, MemberRepository memberRepository) {
        this.rivalRepository = rivalRepository;
        this.memberRepository = memberRepository;
    }

    public void enroll(Long memberId, Long rivalId) {
        if(memberId.equals(rivalId)) {
            throw SameMemberException.of(RivalErrorCode.SAME_MEMBER);
        }

        if(!checkRivalRelationship(memberId, rivalId)) {
            throw ExistRivalException.of(RivalErrorCode.EXIST_RELATIONSHIP);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        Member enemy = memberRepository.findById(rivalId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        Rival rival = new Rival(member, enemy);
        rivalRepository.save(rival);
    }

    public void delete(Long memberId, Long rivalId) {
        if(memberId.equals(rivalId)) {
            throw SameMemberException.of(RivalErrorCode.SAME_MEMBER);
        }

        if(checkRivalRelationship(memberId, rivalId)) {
            throw NotExistRivalException.of(RivalErrorCode.NOT_EXIST_RELATIONSHIP);
        }

        List<Rival> rivals = rivalRepository.findByMemberId(memberId);

        for(Rival rival : rivals) {
            if(rival.getEnemy().getId().equals(rivalId)) {
                rivalRepository.delete(rival);
            }
        }
    }

    public List<?> getRivalList(Long memberId) {
        // 나와 연관된 모든 라이벌들 리스트로 반환
        return null;
    }

    public boolean checkRivalRelationship(Long memberId, Long rivalId) {
        List<Rival> rivals = rivalRepository.findByMemberId(memberId);

        for(Rival rival : rivals) {
            if(rival.getEnemy().getId().equals(rivalId)) {
                return false;
            }
        }
        return true;
    }
}
