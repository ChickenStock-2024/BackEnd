package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.member.dto.MagicNumbers;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import javax.lang.model.SourceVersion;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import org.imgscalr.Scalr;
import com.sascom.chickenstock.domain.member.entity.Image;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final CompetitionRepository competitionRepository;

    @Value("${image.default_img_path}")
    private String default_img_path;

    @Value("${image.default_img_name}")
    private String default_img_name;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    @Autowired
    public MemberService(MemberRepository memberRepository,
                         AccountRepository accountRepository,
                         CompetitionRepository competitionRepository) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.competitionRepository = competitionRepository;
    }

    public MemberInfoResponse lookUpMemberInfo(Long userId) throws IOException{
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

    public PrefixNicknameInfosResponse searchPrefixNicknameMemberInfos(String prefix) throws IOException{
        List<Member> memberList = memberRepository.findFirst10ByNicknameStartingWithOrderByNickname(prefix);

        // 이미지 파일(byte)는 예외 처리가 필요한데 stream에서는 따로 try catch해줘야 해서 for문으로 바꿨습니다.
        List<MemberInfoResponse> result = new ArrayList<>();
        for (Member member : memberList) {
            MemberInfoResponse memberInfoResponse = toMemberInfoResponse(member);
            result.add(memberInfoResponse);
        }

        return new PrefixNicknameInfosResponse(result);
    }

    // Member -> MemberInfoResponse
    private MemberInfoResponse toMemberInfoResponse(Member member) throws IOException {
        return new MemberInfoResponse(
                member.getId(),
                member.getNickname(),
                RatingCalculatorV1.calculateRating(member.getAccounts()),
                member.getPoint()
        );
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



//    public void setImage(Member member, MultipartFile file) throws IOException {
//        if(file.isEmpty()){
//            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            throw MemberNotFoundException.of(MemberErrorCode.NO_FILE);
//        }
//
//        String ImageUuid = UUID.randomUUID().toString();
//        String file_name = ImageUuid + file.getOriginalFilename();
//        String img_path = "C:\\Users\\SSAFY\\Image\\" + file_name;
//        String img_link = "http://localhost:8080" + file_name;
//        File dest = new File(img_path);
//
//        String format = file_name.substring(file_name.lastIndexOf(".")+1);
//        BufferedImage bufferedImage = Scalr.resize(ImageIO.read(file.getInputStream()), 50, 50, Scalr.OP_ANTIALIAS);
//        ImageIO.write(bufferedImage, format, dest);
//
//        Image image = new Image(img_link, file_name, "C:\\Users\\SSAFY\\Image\\");
//        member.updateImage(image);
//        member.updateImageUuid(ImageUuid);
//        memberRepository.save(member);
//    }

    @Transactional
    public void setImage(Long memberId, MultipartFile file) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        if(file.isEmpty()) {
            throw MemberNotFoundException.of(MemberErrorCode.NO_FILE);
        }
        String extension = null;
        byte[] fileBytes = null;
        try {
            fileBytes = file.getBytes();
            if (MagicNumbers.JPG.is(fileBytes)) {
                extension = "jpg";
            }
            else if (MagicNumbers.PNG.is(fileBytes)) {
                extension = "png";
            }
        } catch(IOException e) {
            throw new IllegalStateException("file handle error");
        }
        if(extension == null) {
            throw MemberNotFoundException.of(MemberErrorCode.NO_FILE);
        }
        String fileName = UUID.randomUUID().toString() + LocalDateTime.now().format(formatter)
                + "." + extension;
        member.updateImage(new Image(null, fileName, null));
        memberRepository.save(member);

        File dest = new File(File.separator + "resource" + File.separator + fileName);
        try {
            BufferedImage bufferedImage = Scalr.resize(ImageIO.read(new ByteArrayInputStream(fileBytes)), 800, 600, Scalr.OP_ANTIALIAS);
            ImageIO.write(bufferedImage, extension, dest);
        } catch(IOException e) {
            throw new IllegalStateException("resize handle error");
        }
    }

    public byte[] getImage(Long id) throws IOException{
        Member member = memberRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        Image image = member.getImage();
        // 이미지를 InputStream에 읽어오기
        InputStream inputStream = new FileInputStream(image.getImg_path() + image.getImg_name());

        // InputStream에 읽어 온 이미지를 byte 배열에 저장
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return bytes;
    }

    @Transactional
    public void deleteImage(Long memberId) {
        // soft delete // 이미지 경로만 default로 바꿔줌
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        Image defaultImage = new Image(null, default_img_name, null);
        member.updateImage(defaultImage);
        memberRepository.save(member);
    }
}
