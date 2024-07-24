package com.sascom.chickenstock.domain.company.repository;

import com.sascom.chickenstock.domain.company.entity.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.assertj.core.api.Assertions;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    private Company company;

    @BeforeEach
    void setUp() {
        company = new Company("test company", "123456", null, null);
    }

    @Test
    void testFindById() {
        //given
        companyRepository.save(company);

        // when
        Optional<Company> foundCompany = companyRepository.findById(company.getId());

        // then
        Assertions.assertThat(foundCompany).isPresent();
        Assertions.assertThat(foundCompany.get().getId()).isEqualTo(company.getId());
        Assertions.assertThat(foundCompany.get().getCode()).isEqualTo(company.getCode());
        Assertions.assertThat(foundCompany.get().getName()).isEqualTo(company.getName());
        Assertions.assertThat(foundCompany.get().getStatus()).isEqualTo(company.getStatus());
    }

    @Test
    void testFindByCode() {
        //given
        companyRepository.save(company);

        // when
        Optional<Company> foundCompany = companyRepository.findByCode(company.getCode());

        // then
        Assertions.assertThat(foundCompany).isPresent();
        Assertions.assertThat(foundCompany.get().getId()).isEqualTo(company.getId());
        Assertions.assertThat(foundCompany.get().getCode()).isEqualTo(company.getCode());
        Assertions.assertThat(foundCompany.get().getName()).isEqualTo(company.getName());
        Assertions.assertThat(foundCompany.get().getStatus()).isEqualTo(company.getStatus());
    }

    @Test
    void testFindByNameLike() {
        // given
        companyRepository.save(company);

        // when
        List<Company> companyList = companyRepository.findByNameLike("%Test%");

        // then
        Assertions.assertThat(companyList).isNotEmpty();
        Assertions.assertThat(companyList).hasSize(1);
        Assertions.assertThat(companyList.get(0).getId()).isEqualTo(company.getId());
        Assertions.assertThat(companyList.get(0).getCode()).isEqualTo(company.getCode());
        Assertions.assertThat(companyList.get(0).getName()).isEqualTo(company.getName());
        Assertions.assertThat(companyList.get(0).getStatus()).isEqualTo(company.getStatus());
    }
}
