package com.sascom.chickenstock.domain.rival.service;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import com.sascom.chickenstock.domain.rival.dto.request.RequestEnrollRivalDTO;
import com.sascom.chickenstock.domain.rival.dto.response.ResponseEnrollRivalDTO;
import com.sascom.chickenstock.domain.rival.entity.Rival;
import com.sascom.chickenstock.domain.rival.repository.RivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RivalService {

    private final MemberRepository memberRepository;
    private final RivalRepository rivalRepository;

    public void addRivalByRivalId(Long rivalId) {
        // 나의 memberID를 받았다고 친다. 임시로 만들어 둠
        // TODO: 아래 member 부분은 나중에 spring security 관련 util을 만들어서 거기서 처리하면 좋을 거 같습니다.
        Member member = getMemberFromContextHolder()
                .orElseThrow(() -> new IllegalStateException("Authorization Error"));
        Member enemy = memberRepository.findById(rivalId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        // validation
        if(member.equals(enemy)) {
            // TODO: IllegalStateException -> RivalException
            throw new IllegalStateException("self rival");
        }
        Rival rival = rivalRepository.findByMemberAndEnemy(member, enemy)
                .orElseGet(() -> new Rival(member, enemy));
        rivalRepository.save(rival);
    }

    public void removeRivalByRivalId(Long rivalId) {
        // TODO: addRivalByRivalId와 마찬가지
        Member member = getMemberFromContextHolder()
                .orElseThrow(() -> new IllegalStateException("Authorization Error"));
        Member enemy = memberRepository.findById(rivalId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        // validation
        if(member.equals(enemy)) {
            // TODO: IllegalStateException -> RivalException
            throw new IllegalStateException("self rival");
        }
        rivalRepository.findByMemberAndEnemy(member, enemy)
                .ifPresent(rivalRepository::delete);
    }

    public List<ResponseEnrollRivalDTO> getRivalList() {
        Member member = getMemberFromContextHolder()
                .orElseThrow(() -> new IllegalStateException("Authorization Error"));
        List<Rival> rivals = rivalRepository.findByMember(member);
        // TODO: MemberService, Repository를 손 보고 MemberRankingDto로 잘 변환해서 return...
        return null;
    }

    public boolean check(Long rivalId) {
        Member member = getMemberFromContextHolder()
                .orElseThrow(() -> new IllegalStateException("Authorization Error"));
        Member enemy = memberRepository.findById(rivalId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        Rival rival = rivalRepository.findByMemberAndEnemy(member, enemy)
                .orElse(null);
        return rival != null;
    }

    private Optional<Member> getMemberFromContextHolder() {
//         Long memberId = (CustomUserDetails)(SecurityContextHolder.getContext().getAuthentication())
//                .getPrincipal()
//                .getMemberId();
        Long memberId = 1L;
        // TODO: global exception
        return memberRepository.findById(memberId);
    }
}
