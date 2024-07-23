package com.sascom.chickenstock.domain.rival.controller;

import com.sascom.chickenstock.domain.rival.dto.request.RequestEnrollRivalDTO;
import com.sascom.chickenstock.domain.rival.dto.response.ResponseEnrollRivalDTO;
import com.sascom.chickenstock.domain.rival.service.RivalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/rival")
public class RivalController {

    private RivalService rivalService;

    @PostMapping
    public ResponseEntity<ResponseEnrollRivalDTO> enroll(@RequestBody RequestEnrollRivalDTO requestEnrollRivalDTO) {
        rivalService.save(requestEnrollRivalDTO);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{rivalId}")
    public ResponseEntity<ResponseEnrollRivalDTO> delete(@PathVariable(name = "rivalId") Long rivalId) {
        rivalService.delete(rivalId);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ResponseEnrollRivalDTO>> getList() {
        List<ResponseEnrollRivalDTO> rivals = rivalService.getRivalList();
        // id, 닉네임, 랭킹, 수익률, 대회참여횟수, 경험치... 라이벌 멤버 정보를 리스트로 반환해야한다.

        return ResponseEntity.ok().body(rivals);
    }

    @GetMapping("/{rivalId}")
    public ResponseEntity<Map<String, Boolean>> check(@PathVariable(name = "rivalId") Long id) {
        boolean result = rivalService.check(id);

        return ResponseEntity.ok().body(Map.of("is_rival", result));
    }


}
