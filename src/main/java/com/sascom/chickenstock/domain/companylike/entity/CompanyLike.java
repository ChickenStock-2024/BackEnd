package com.sascom.chickenstock.domain.companylike.entity;


import com.sascom.chickenstock.domain.company.entity.Company;
import com.sascom.chickenstock.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "companylike")
public class CompanyLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "companylike_id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    public CompanyLike(Member member, Company company) {
        this.member = member;
        this.company = company;
    }
}

