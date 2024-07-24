package com.sascom.chickenstock.domain.company.entity;


import com.sascom.chickenstock.domain.companylike.entity.CompanyLike;
import com.sascom.chickenstock.domain.history.entity.History;
import com.sascom.chickenstock.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "company")
public class Company extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "code", unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CompanyStatus status;

    @OneToMany(mappedBy = "company")
    private List<History> histories;

    @OneToMany(mappedBy = "company")
    private List<CompanyLike> companyLikes;

    public Company(String name, String code) {
        this.name = name;
        this.code = code;
    }

    @PrePersist
    protected void onCreate() {
        status = CompanyStatus.LISTED;
    }
}
