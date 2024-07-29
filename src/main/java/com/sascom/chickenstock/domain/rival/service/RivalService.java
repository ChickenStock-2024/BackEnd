package com.sascom.chickenstock.domain.rival.service;

import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
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
        // 나의 memberID를 받았다고 친다.
        // 아래 member 부분은 나중에 spring security 관련 util을 만들어서 거기서 처리하면 좋을 거 같습니다.
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
        // 내 id와 라이벌 id가 매핑된 행을 지운다.
    }

    public List<ResponseEnrollRivalDTO> getRivalList() {
        // 나와 연관된 모든 라이벌들 리스트로 반환
        return null;
    }

    public boolean check(Long id) {
        // 나와 해당 id와 라이벌인지 체크
        return true;
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
