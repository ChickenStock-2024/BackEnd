package com.sascom.chickenstock.domain.member.controller;

import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.member.dto.request.ChangePasswordRequest;
import com.sascom.chickenstock.domain.member.dto.request.ChangeNicknameRequest;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.service.MemberFacade;
import com.sascom.chickenstock.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


@RestController
@RequestMapping("/user")
public class MemberController {

    private final MemberService memberService;
    private final MemberFacade memberFacade;

    @Autowired
    public MemberController(MemberService memberService, MemberFacade memberFacade) {
        this.memberService = memberService;
        this.memberFacade = memberFacade;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<MemberInfoResponse> getMemberInfo(@PathVariable("userId") Long userId) {
        MemberInfoResponse response = memberService.lookUpMemberInfo(userId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping
    public ResponseEntity<ResponseLoginMember> getMemberInfo(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ResponseLoginMember loginInfo = memberFacade.getLoginInfo(response, authentication);
        return ResponseEntity.ok(loginInfo);
    }

    @PostMapping
    public ResponseEntity<String> patchPassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        memberService.changePassword(changePasswordRequest);
        return ResponseEntity.ok().body("비밀번호 변경 완료");
    }

    @GetMapping("/search")
    public ResponseEntity<PrefixNicknameInfosResponse> getPrefixNicknameMemberInfos(
            @RequestParam(name = "value") String prefix) {
        PrefixNicknameInfosResponse prefixNicknameInfosResponse = memberService.searchPrefixNicknameMemberInfos(prefix);
        return ResponseEntity.ok().body(prefixNicknameInfosResponse);
    }

    @PostMapping("/nickname")
    public ResponseEntity<Map<String, String>> patchNickname(@RequestBody ChangeNicknameRequest changeNicknameRequest) {
        String changedNickname = memberService.changeNickname(changeNicknameRequest.nickname());
        return ResponseEntity.ok().body(Map.of("nickname", changedNickname));
    }

    @PostMapping("/noti/web")
    public ResponseEntity<Map<String, Boolean>> patchWebNotification() {
        boolean changedState = memberService.toggleWebNotification();
        return ResponseEntity.ok().body(Map.of("webNoti", changedState));
    }

    @PostMapping("/noti/kakaotalk")
    public ResponseEntity<Map<String, Boolean>> patchKakaotalkNotification() {
        boolean changedState = memberService.toggleKakaotalkNotification();
        return ResponseEntity.ok().body(Map.of("kakaotalkNoti", changedState));
    }

    @PostMapping(value = "/img", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> postImage(@RequestParam("file") MultipartFile file) {
        String changedUrl = memberService.setImage(file);
        return ResponseEntity.ok().body(Map.of("imgUrl", changedUrl));
    }

    @GetMapping(value = "/img/{userId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> getImage(@PathVariable("userId") Long id) {
        byte[] bytes = memberService.getImage(id);
        return ResponseEntity.ok().body(bytes);
    }

    @PostMapping(value = "/img/delete")
    public ResponseEntity<Void> deleteImage() {
        memberService.deleteImage();
        return ResponseEntity.ok().build();
    }

}
