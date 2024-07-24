package com.sascom.chickenstock.domain.company.service;

import com.sascom.chickenstock.domain.company.dto.response.CompanyInfoResponse;
import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.company.error.code.CompanyErrorCode;
import com.sascom.chickenstock.domain.company.error.exception.CompanyException;
import com.sascom.chickenstock.domain.company.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.Assertions;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    private Company company;

    @BeforeEach
    void setUp() {
        company = Mockito.spy(new Company("test company", "123456"));
    }

    @Test
    void testGetCompanyInfo_Success() {
        // given
        when(company.getId()).thenReturn(1L);
        when(companyRepository.findById(anyLong())).thenReturn(Optional.of(company));

        // when
        CompanyInfoResponse response = companyService.getCompanyInfo(1L);

        // then
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.id()).isEqualTo(company.getId());
        Assertions.assertThat(response.code()).isEqualTo(company.getCode());
        Assertions.assertThat(response.name()).isEqualTo(company.getName());
        Assertions.assertThat(response.status()).isEqualTo(company.getStatus());

        verify(companyRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetCompanyInfo_NotFound() {
        // given
        when(companyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Throwable thrown = Assertions.catchThrowable(() -> companyService.getCompanyInfo(2L));

        // then
        Assertions.assertThat(thrown)
                .isInstanceOf(CompanyException.class)
                .hasFieldOrPropertyWithValue("errorCode", CompanyErrorCode.NOT_FOUND);

        verify(companyRepository, times(1)).findById(anyLong());
    }

}
