package com.sascom.chickenstock.domain.rival.service;

import com.sascom.chickenstock.domain.rival.dto.request.RequestEnrollRivalDTO;
import com.sascom.chickenstock.domain.rival.dto.response.ResponseEnrollRivalDTO;
import com.sascom.chickenstock.domain.rival.repository.RivalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RivalService {

    private RivalRepository rivalRepository;

    public void save(RequestEnrollRivalDTO requestEnrollRivalDTO) {
        // 나의 memberID를 받았다고 친다.
        // 나와 라이벌id를 통해서 Member객체 만들고 저장한다.
        //rivalRepository.save(new Rival());
    }

    public void delete(Long id) {
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
}
