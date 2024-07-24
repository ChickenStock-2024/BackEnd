package com.sascom.chickenstock.domain.member.controller;

import com.sascom.chickenstock.domain.member.dto.request.ChangeInfoRequest;
import com.sascom.chickenstock.domain.member.dto.response.ChangeInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class MemberController {

    private MemberService memberService;

    @GetMapping("/{userId}")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@PathVariable("userId") Long userId) {
        MemberInfoResponse response = memberService.getMemberInfo(userId);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ChangeInfoResponse> changeMemberInfo(
            @PathVariable("userId") Long userId,
            @RequestBody ChangeInfoRequest changeInfoRequest){
        ChangeInfoResponse changeInfoResponse = memberService.changeMemberInfo(userId, changeInfoRequest);
        return ResponseEntity.ok().body(changeInfoResponse);
    }
}
