package com.sascom.chickenstock.domain.member.controller;

import com.sascom.chickenstock.domain.member.dto.request.ChangeInfoRequest;
import com.sascom.chickenstock.domain.member.dto.response.ChangeInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@PathVariable("userId") Long userId) {
        MemberInfoResponse response = memberService.lookUpMemberInfo(userId);
        return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ChangeInfoResponse> patchMemberInfo(
            @PathVariable("userId") Long userId,
            @RequestBody ChangeInfoRequest changeInfoRequest) {
        ChangeInfoResponse changeInfoResponse = memberService.changeMemberInfo(userId, changeInfoRequest);
        return ResponseEntity.ok().body(changeInfoResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<PrefixNicknameInfosResponse> getPrefixNicknameMemberInfos(
            @RequestParam(name = "value") String prefix) {
        PrefixNicknameInfosResponse prefixNicknameInfosResponse = memberService.searchPrefixNicknameMemberInfos(prefix);
        return ResponseEntity.ok().body(prefixNicknameInfosResponse);
    }
}
