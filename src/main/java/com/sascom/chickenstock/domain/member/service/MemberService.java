package com.sascom.chickenstock.domain.member.service;

import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.competition.repository.CompetitionRepository;
import com.sascom.chickenstock.domain.member.dto.MagicNumbers;
import com.sascom.chickenstock.domain.member.dto.MemberInfoForLogin;
import com.sascom.chickenstock.domain.member.dto.request.ChangePasswordRequest;
import com.sascom.chickenstock.domain.member.dto.response.MemberInfoResponse;
import com.sascom.chickenstock.domain.member.dto.response.PrefixNicknameInfosResponse;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.error.code.MemberErrorCode;
import com.sascom.chickenstock.domain.member.error.exception.MemberImageException;
import com.sascom.chickenstock.domain.member.error.exception.MemberNotFoundException;
import com.sascom.chickenstock.domain.member.error.exception.MemberInfoChangeException;
import com.sascom.chickenstock.domain.member.repository.MemberRepository;
import com.sascom.chickenstock.domain.ranking.dto.MemberRankingDto;
import com.sascom.chickenstock.domain.ranking.service.RankingService;
import com.sascom.chickenstock.domain.ranking.util.RatingCalculatorV1;
import com.sascom.chickenstock.global.error.code.AuthErrorCode;
import com.sascom.chickenstock.global.error.exception.AuthException;
import com.sascom.chickenstock.global.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final RankingService rankingService;
    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;
    private final CompetitionRepository competitionRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${image.url}")
    private String imgUrl;

    @Value("${image.directories}")
    private String uploadPath;

    @Value("${image.default-img-name}")
    private String defaultImgName;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    public MemberService(
            RankingService rankingService,
            MemberRepository memberRepository,
            AccountRepository accountRepository,
            CompetitionRepository competitionRepository,
            PasswordEncoder passwordEncoder) {
        this.rankingService = rankingService;
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
        this.competitionRepository = competitionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        uploadPath = File.separator + String.join(File.separator, uploadPath.split(","));
        File uploadDirectory = new File(uploadPath);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs(); // 디렉터리 생성
        }
    }

    public MemberInfoResponse lookUpMemberInfo(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        return toMemberInfoResponse(member);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));

        if (changePasswordRequest.newPassword() == null ||
                !changePasswordRequest.newPassword().equals(changePasswordRequest.newPasswordCheck())) {
            throw MemberInfoChangeException.of(MemberErrorCode.PASSWORD_CONFIRMATION_ERROR);
        }
        if (changePasswordRequest.oldPassword() == null ||
                !passwordEncoder.matches(changePasswordRequest.oldPassword(), member.getPassword())) {
            throw MemberInfoChangeException.of(MemberErrorCode.INCORRECT_PASSWORD);
        }
        if (!isSafePassword(changePasswordRequest.newPassword())) {
            throw new IllegalStateException("New Password is not safe.");
        }

        String hashedNewPassword = passwordEncoder.encode(changePasswordRequest.newPassword());
        member.updatePassword(hashedNewPassword);
        Member savedMember = memberRepository.save(member);
    }

    @Transactional
    public String changeNickname(String nickname) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        if (memberRepository.existsByNickname(nickname)) {
            throw AuthException.of(AuthErrorCode.NICKNAME_CONFLICT);
        }
        if (!isAvailableNickname(nickname)) {
            throw MemberInfoChangeException.of(MemberErrorCode.UNAVAILABLE_NICKNAME);
        }
        member.updateNickname(nickname);
        memberRepository.save(member);
        return nickname;
    }

    public PrefixNicknameInfosResponse searchPrefixNicknameMemberInfos(String prefix) {
        List<Member> memberList = memberRepository.findFirst10ByNicknameStartingWithOrderByNickname(prefix);

        return new PrefixNicknameInfosResponse(
                memberList.stream()
                        .map(this::toMemberInfoResponse)
                        .toList()
        );
    }

    @Transactional
    public boolean toggleWebNotification() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        boolean result = member.toggleWebNoti();
        memberRepository.save(member);
        return result;
    }

    @Transactional
    public boolean toggleKakaotalkNotification() {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        boolean result = member.toggleKakaotalkNoti();
        memberRepository.save(member);
        return result;
    }

    // Member -> MemberInfoResponse
    private MemberInfoResponse toMemberInfoResponse(Member member) {
        MemberRankingDto rankingDto = rankingService.getRankingById(member.getId());
        return new MemberInfoResponse(
                rankingDto.getMemberId(),
                rankingDto.getNickname(),
                rankingDto.getRating(),
                rankingDto.getProfit(),
                rankingDto.getRanking(),
                member.getPoint(),
                imgUrl + member.getImgName()
        );
    }

    // check that given new password is fit to safe password standard.
    // eg) contains at least 3 alphabets, at least one of special symbols as !, @, #, $, ... .
    private boolean isSafePassword(String password) {
        // TODO: implementation
        return true;
    }

    private boolean isAvailableNickname(String nickname) {
        // TODO: implementation
        return true;
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
    }

    @Transactional
    public String setImage(MultipartFile file) {
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        if (file.isEmpty()) {
            throw MemberNotFoundException.of(MemberErrorCode.NO_FILE);
        }

        byte[] fileBytes = null;
        String extension = null;
        try {
            fileBytes = file.getBytes();
            if (MagicNumbers.JPG.is(fileBytes)) {
                extension = "jpg";
            } else if (MagicNumbers.PNG.is(fileBytes)) {
                extension = "png";
            }
        } catch (IOException e) {
            throw MemberImageException.of(MemberErrorCode.IO_ERROR);
        }

        if (extension == null) {
            throw MemberNotFoundException.of(MemberErrorCode.INVALID_FILE);
        }

        String fileName = UUID.randomUUID().toString() + LocalDateTime.now().format(formatter)
                + "." + extension;
        member.updateImgName(fileName);
        memberRepository.save(member);

        try {
            saveFile(fileBytes, fileName, extension);
        } catch (IOException e) {
            throw MemberImageException.of(MemberErrorCode.IO_ERROR);
        }
        return imgUrl + fileName;
    }

    public byte[] getImage(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        String imgName = member.getImgName();
        // 이미지를 InputStream에 읽어오기
        try (InputStream inputStream = new FileInputStream(uploadPath + File.separator + imgName)) {
            byte[] bytes = inputStream.readAllBytes();
            return bytes;
        } catch (IOException e) {
            throw MemberImageException.of(MemberErrorCode.IO_ERROR);
        }
    }

    @Transactional
    public void deleteImage() {
        // soft delete // 이미지 경로만 default로 바꿔줌
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        member.updateImgName(defaultImgName);
        memberRepository.save(member);
    }

    private void saveFile(byte[] fileBytes, String fileName, String extension) throws IOException {
        int TARGET_IMAGE_WIDTH = 200, TARGET_IMAGE_HEIGHT = 200;

        File dest = new File(uploadPath + File.separator + fileName);
        BufferedImage bufferedImage = Scalr.resize(
                ImageIO.read(new ByteArrayInputStream(fileBytes)),
                TARGET_IMAGE_WIDTH,
                TARGET_IMAGE_HEIGHT,
                Scalr.OP_ANTIALIAS
        );
        ImageIO.write(bufferedImage, extension, dest);
    }

    public MemberInfoForLogin lookUpMemberInfoForLogin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        return new MemberInfoForLogin(
                member.getId(), member.getNickname(),
                member.isWebNoti(), member.isKakaotalkNoti()
        );
    }

    public String getMemberEmail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MemberNotFoundException.of(MemberErrorCode.NOT_FOUND));
        return member.getEmail();
    }
}