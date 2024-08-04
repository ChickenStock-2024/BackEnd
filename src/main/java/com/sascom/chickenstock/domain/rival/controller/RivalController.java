package com.sascom.chickenstock.domain.rival.controller;

import com.sascom.chickenstock.domain.rival.service.RivalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rival")
public class RivalController {

    private final RivalService rivalService;

    @Autowired
    private RivalController(RivalService rivalService) {
        this.rivalService = rivalService;
    }

    /*
    나중에 Spring Security에서 memberID 조회되면 제거
     */
    @PostMapping("/{member_id}/{rival_id}")
    public ResponseEntity<?> enroll(@PathVariable("member_id") Long memberId, @PathVariable("rival_id") Long rivalId) {
        rivalService.enroll(memberId, rivalId);

        return ResponseEntity.ok().build();
    }

    /*
    나중에 Spring Security에서 memberID 조회되면 제거
     */
    @DeleteMapping("/{member_id}/{rival_id}")
    public ResponseEntity<?> delete(@PathVariable("member_id") Long memberId, @PathVariable("rival_id") Long rivalId) {
        rivalService.delete(memberId, rivalId);

        return ResponseEntity.ok().build();
    }

    /*
    나중에 Spring Security에서 memberID 조회되면 제거
     */
    @GetMapping("/{member_id}")
    public ResponseEntity<?> getList(@PathVariable("member_id") Long memberId) {
        
        // id, 닉네임, 랭킹, 수익률, 대회참여횟수, 경험치... 라이벌 멤버 정보를 리스트로 반환해야한다.

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{rivalId}")
    public void check(@PathVariable(name = "rivalId") Long id) {
        // boolean result = rivalService.check(id);

        // return ResponseEntity.ok().body(Map.of("is_rival", result));
    }


}
