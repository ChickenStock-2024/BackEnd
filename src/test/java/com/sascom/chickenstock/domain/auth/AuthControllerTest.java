package com.sascom.chickenstock.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.account.repository.AccountRepository;
import com.sascom.chickenstock.domain.account.service.AccountService;
import com.sascom.chickenstock.domain.auth.controller.AuthController;
import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.response.AccountInfoForLogin;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.auth.service.AuthService;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import com.sascom.chickenstock.domain.member.entity.Member;
import com.sascom.chickenstock.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @Mock
    private MemberService memberService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testSignup() throws Exception {
        RequestSignupMember requestSignupMember = new RequestSignupMember("test@example.com", "password", "password", "TestUser");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestSignupMember)))
                .andExpect(status().isCreated())
                .andExpect(content().string("회원가입이 처리되었습니다!"));
    }

    @Test
    void testLogin() throws Exception {
        RequestLoginMember requestLoginMember = new RequestLoginMember("test@example.com", "password");
        TokenDto tokenDto = new TokenDto("testAccessToken", "testRefreshToken");
        Member member = new Member("TestUser", "test@example.com", "encodedPassword");
        String startAtString = "2024-08-05T08:00:00";
        String endAtString = "2024-08-15T08:00:00";

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime startAt = LocalDateTime.parse(startAtString, formatter);
        LocalDateTime endAt = LocalDateTime.parse(endAtString, formatter);
        Competition competition = new Competition("1회 대회", startAt, endAt);
        Account account = new Account(member, competition);

        // Mock 설정
        when(authService.login(any(RequestLoginMember.class))).thenReturn(tokenDto);
        when(memberService.findByEmail(any(String.class))).thenReturn(member);
        // 여기서 올바른 값을 반환하도록 설정
        AccountInfoForLogin accountInfoForLogin = AccountInfoForLogin.create(true, 50000000L, 1200);
        when(accountService.getInfoForLogin(any(Long.class))).thenReturn(accountInfoForLogin);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLoginMember)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", "Access-token=testAccessToken; Path=/; HttpOnly"))
                .andExpect(header().string("Set-Cookie", "Refresh-token=testRefreshToken; Path=/; HttpOnly"))
                .andExpect(jsonPath("$.isCompParticipant").value(true))
                .andExpect(jsonPath("$.balance").value(50000000L))
                .andExpect(jsonPath("$.rating").value(1200))
                .andExpect(jsonPath("$.nickName").value("TestUser"));
    }


}