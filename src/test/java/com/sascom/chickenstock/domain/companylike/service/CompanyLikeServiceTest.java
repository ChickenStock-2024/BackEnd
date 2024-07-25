package com.sascom.chickenstock.domain.companylike.service;

import com.sascom.chickenstock.domain.company.dto.response.CompanyInfoResponse;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyNotFoundException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import com.sascom.chickenstock.domain.company.service.CompanyService;
import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.member.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class CompanyLikeServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private Company company;
    private Member member;
    private CompanyLike companyLike;

    @BeforeEach
    void setUp() {
        company = Mockito.spy(new Company("test company", "123456"));
        member = Mockito.spy(new Member("nickname", "eee@ddd.com", "1234"));
        companyLike = Mockito.spy(new CompanyLike(member, company));
    }

    @Test
    void testGetCompanyInfo_Success() {
//        // given
//        when(company.getId()).thenReturn(1L);
//        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));
//
//        // when
//        CompanyInfoResponse response = companyService.getCompanyInfo(1L);
//
//        // then
//        Assertions.assertThat(response).isNotNull();
//        Assertions.assertThat(response.id()).isEqualTo(company.getId());
//        Assertions.assertThat(response.code()).isEqualTo(company.getCode());
//        Assertions.assertThat(response.name()).isEqualTo(company.getName());
//        Assertions.assertThat(response.status()).isEqualTo(company.getStatus());
//
//        verify(companyRepository, times(1)).findById(anyLong());
    }
}
