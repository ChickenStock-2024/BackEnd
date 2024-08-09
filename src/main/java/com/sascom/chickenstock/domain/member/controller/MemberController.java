package com.sascom.chickenstock.domain.member.controller;

import ch.qos.logback.core.util.FileUtil;
import com.sascom.chickenstock.domain.member.dto.request.ChangeInfoRequest;
import com.sascom.chickenstock.domain.member.dto.response.ChangeInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.service.MemberService;
import com.sascom.chickenstock.global.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@PathVariable("userId") Long userId) throws IOException{
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
            @RequestParam(name = "value") String prefix) throws IOException{
        PrefixNicknameInfosResponse prefixNicknameInfosResponse = memberService.searchPrefixNicknameMemberInfos(prefix);
        return ResponseEntity.ok().body(prefixNicknameInfosResponse);
    }

    @PostMapping(value = "/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> postImage(@RequestParam("file") MultipartFile file) throws IOException {
//        Member member = memberService.findById(SecurityUtil.getCurrentMemberId());
//        memberService.setImage(member, file);
//        return ResponseEntity.ok()
//                .body("프로필 이미지 업로드 완료");
        // validate file
        memberService.setImage(SecurityUtil.getCurrentMemberId(), file);
        return ResponseEntity.ok().body(Map.of("msg", "프로필 이미지 업로드 완료"));
    }

    @GetMapping(value = "/img/{userId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable("userId") Long id) throws IOException {
        byte[] bytes = memberService.getImage(id);
        return new ResponseEntity<byte[]>(bytes, HttpStatus.OK);
    }

    @PostMapping(value = "/img/delete")
    public ResponseEntity<?> deleteImage() {
        memberService.deleteImage(SecurityUtil.getCurrentMemberId());
        return ResponseEntity.ok().build();
    }
}
