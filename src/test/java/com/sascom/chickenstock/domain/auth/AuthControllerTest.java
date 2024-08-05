package com.sascom.chickenstock.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sascom.chickenstock.domain.auth.controller.AuthController;
import com.sascom.chickenstock.domain.auth.dto.request.RequestLoginMember;
import com.sascom.chickenstock.domain.auth.dto.request.RequestSignupMember;
import com.sascom.chickenstock.domain.auth.dto.response.ResponseLoginMember;
import com.sascom.chickenstock.domain.auth.dto.token.TokenDto;
import com.sascom.chickenstock.domain.auth.service.AuthService;
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
        TokenDto tokenDto = TokenDto.builder()
                .grantType("Bearer")
                .accessToken("testAccessToken")
                .refreshToken("testRefreshToken")
                .accessTokenExpiresIn(LocalDateTime.now().plusHours(1))
                .build();
        Member member = new Member("TestUser", "test@example.com", "encodedPassword");
        ResponseLoginMember responseLoginMember = new ResponseLoginMember(member);

        when(authService.login(any(RequestLoginMember.class))).thenReturn(tokenDto);
        when(memberService.findByEmail(any(String.class))).thenReturn(member);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLoginMember)))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-token", "testAccessToken"))
                .andExpect(jsonPath("$.memberId").value(member.getId()))
                .andExpect(jsonPath("$.nickName").value("TestUser"));
    }
}