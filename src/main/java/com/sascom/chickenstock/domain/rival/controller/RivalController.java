package com.sascom.chickenstock.domain.rival.controller;

import com.sascom.chickenstock.domain.rival.dto.response.CheckRivalResponse;
import com.sascom.chickenstock.domain.rival.dto.response.RivalMemberInfoResponse;
import com.sascom.chickenstock.domain.rival.service.RivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rival")
public class RivalController {

    private final RivalService rivalService;

    @Autowired
    private RivalController(RivalService rivalService) {
        this.rivalService = rivalService;
    }

    @PostMapping("/{rival_id}")
    public ResponseEntity<Boolean> enroll(@PathVariable("rival_id") Long rivalId) {
        rivalService.enroll(rivalId);

        return ResponseEntity.ok().body(true);
    }

    @DeleteMapping("/{rival_id}")
    public ResponseEntity<Boolean> delete(@PathVariable("rival_id") Long rivalId) {
        rivalService.delete(rivalId);

        return ResponseEntity.ok().body(false);
    }

    @GetMapping
    public ResponseEntity<List<RivalMemberInfoResponse>> getList() {

        // id, 닉네임, 랭킹, 수익률, 대회참여횟수, 경험치... 라이벌 멤버 정보를 리스트로 반환해야한다.
        // 현재 id와 닉네임만 반환중....
        List<RivalMemberInfoResponse> rivalMemberInfoResponses = rivalService.getRivalList();

        return ResponseEntity.ok().body(rivalMemberInfoResponses);
    }

    @GetMapping("/{rival_id}")
    public ResponseEntity<CheckRivalResponse> check(@PathVariable("rival_id") Long rivalId) {
        CheckRivalResponse checkRivalResponse = rivalService.checkRival(rivalId);

        return ResponseEntity.ok().body(checkRivalResponse);
    }


}
