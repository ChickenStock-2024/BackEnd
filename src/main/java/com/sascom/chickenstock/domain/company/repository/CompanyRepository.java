package com.sascom.chickenstock.domain.company.repository;

import com.sascom.chickenstock.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCode(String code);
    List<Company> findByNameContains(String name);
}
