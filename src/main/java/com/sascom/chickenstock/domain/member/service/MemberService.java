package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.member.dto.request.ChangeInfoRequest;
import com.sascom.chickenstock.domain.member.dto.response.ChangeInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.MemberExceptionHandler;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.util.RatingCalculatorV1;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.imgscalr.Scalr;
import com.sascom.chickenstock.domain.member.entity.Image;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final CompetitionRepository competitionRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository,
                         AccountRepository accountRepository,
                         CompetitionRepository competitionRepository) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.competitionRepository = competitionRepository;
    }

    public MemberInfoResponse lookUpMemberInfo(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("invalid userId"));

        return toMemberInfoResponse(member);
    }

    @Transactional
    public ChangeInfoResponse changeMemberInfo(
            Long userId,
            ChangeInfoRequest changeInfoRequest) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("invalid userId"));
        // hash given password or use spring security

        if(!changeInfoRequest.oldPassword().equals(member.getPassword())) {
            throw new IllegalStateException("incorrect Old Password");
        }
        if(changeInfoRequest.newPassword() == null ||
                !changeInfoRequest.newPassword().equals(
                changeInfoRequest.newPasswordCheck())) {
            throw new IllegalStateException("New Password and New Password Check are not equal");
        }
        if(!isSafePassword(changeInfoRequest.newPassword())){
            throw new IllegalStateException("New Password is not safe.");
        }

        // hash new password, also. need to edit below code.
        String hashedNewPassword = changeInfoRequest.newPassword();
        member.updatePassword(hashedNewPassword);
        Member savedMember = memberRepository.save(member);
        return new ChangeInfoResponse(savedMember.getNickname());
    }

    public PrefixNicknameInfosResponse searchPrefixNicknameMemberInfos(String prefix) {
        List<Member> memberList = memberRepository.findFirst10ByNicknameStartingWithOrderByNickname(prefix);

        List<MemberInfoResponse> result = memberList.stream()
                .map(this::toMemberInfoResponse)
                .collect(Collectors.toList());

        return new PrefixNicknameInfosResponse(result);
    }

    // Member -> MemberInfoResponse
    private MemberInfoResponse toMemberInfoResponse(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getNickname(),
                RatingCalculatorV1.calculateRating(member.getAccounts()),
                member.getPoint());
    }

    // check that given new password is fit to safe password standard.
    // eg) contains at least 3 alphabets, at least one of special symbols as !, @, #, $, ... .
    private boolean isSafePassword(String password) {
        // TODO: implementation
        return true;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(EntityNotFoundException::new);
    }

    public Member findById(Long userId){
        return memberRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
    }

    public void setImage(Member member, MultipartFile file) throws IOException {
        if(file.isEmpty()){
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            throw MemberNotFoundException.of(MemberErrorCode.NO_FILE);
        }

        LocalDateTime localDateTime = LocalDateTime.now();
        String file_name = localDateTime + file.getOriginalFilename();
        String img_path = "/member/img" + file_name;
        String img_link = "https:/도메인/" + file_name;
        File dest = new File(img_path);

        String format = file_name.substring(file_name.lastIndexOf(".")+1);
        BufferedImage bufferedImage = Scalr.resize(ImageIO.read(file.getInputStream()), 1000, 1000, Scalr.OP_ANTIALIAS);
        ImageIO.write(bufferedImage, format, dest);

        Image image = new Image(img_link, file_name, img_path);
        member.updateImage(image);
        memberRepository.save(member);
    }
}
