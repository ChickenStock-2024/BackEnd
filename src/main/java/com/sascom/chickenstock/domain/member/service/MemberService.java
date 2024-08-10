package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.member.dto.MagicNumbers;
import com.sascom.chickenstock.domain.member.dto.request.ChangeInfoRequest;
import com.sascom.chickenstock.domain.member.dto.response.ChangeInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.util.RatingCalculatorV1;
import com.sascom.chickenstock.global.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import org.imgscalr.Scalr;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final CompetitionRepository competitionRepository;

    @Value("${image.url}")
    private String imgUrl;

    @Value("${image.directories}")
    private String uploadPath;

    @Value("${image.default-img-name}")
    private String defaultImgName;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    
    @Autowired
    public MemberService(MemberRepository memberRepository,
                         AccountRepository accountRepository,
                         CompetitionRepository competitionRepository) {
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.competitionRepository = competitionRepository;
    }

    @PostConstruct
    public void init() {
        uploadPath = File.separator + String.join(File.separator, uploadPath.split(","));
        System.out.println(uploadPath);
        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs(); // 디렉터리 생성
        }
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

    @Transactional
    public String changeNickname(String nickname) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        // validate nickname

        member.updateNickname(nickname);
        memberRepository.save(member);
        return nickname;
    }

    public PrefixNicknameInfosResponse searchPrefixNicknameMemberInfos(String prefix) throws IOException{
        List<Member> memberList = memberRepository.findFirst10ByNicknameStartingWithOrderByNickname(prefix);

        return new PrefixNicknameInfosResponse(
                memberList.stream()
                        .map(this::toMemberInfoResponse)
                        .toList()
        );
    }

    // Member -> MemberInfoResponse
    private MemberInfoResponse toMemberInfoResponse(Member member) {
        return new MemberInfoResponse(
                member.getId(),
                member.getNickname(),
                RatingCalculatorV1.calculateRating(member.getAccounts()),
                member.getPoint(),
                imgUrl + "/" + member.getImgName()
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
    public void setImage(MultipartFile file) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
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
        member.updateImgName(fileName);
        memberRepository.save(member);

        File dest = new File(uploadPath + File.separator + fileName);
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
        String imgName = member.getImgName();
        // 이미지를 InputStream에 읽어오기
        InputStream inputStream = new FileInputStream(uploadPath + File.separator + imgName);

        // InputStream에 읽어 온 이미지를 byte 배열에 저장
        byte[] bytes = inputStream.readAllBytes();
        inputStream.close();
        return bytes;
    }

    @Transactional
    public void deleteImage() {
        // soft delete // 이미지 경로만 default로 바꿔줌
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        member.updateImgName(defaultImgName);
        memberRepository.save(member);
    }
}
