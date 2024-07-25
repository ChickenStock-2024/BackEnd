package com.sascom.chickenstock.domain.companylike.repository;

import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyLikeRepository extends JpaRepository<CompanyLike, Long> {
    CompanyLike findByCompanyIdAndMemberId(Long company_id, Long member_id);
}
