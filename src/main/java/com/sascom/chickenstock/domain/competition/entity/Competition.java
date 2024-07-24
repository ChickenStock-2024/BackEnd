package com.sascom.chickenstock.domain.competition.entity;


import com.sascom.chickenstock.domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.dialect.function.TruncFunction;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "competition")
public class Competition  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "competition_id")
    private Long id;

    @NotNull
    @Column(name = "title")
    private String title;

    @NotNull
    @Column(name = "start_at")
    private Date startAt;

    @NotNull
    @Column(name = "end_at")
    private Date endAt;

    @OneToMany(mappedBy = "competition")
    private List<Account> accounts;

    @Column(name = "total_people")
    private Integer total_people;

    public Competition(String title, Date startAt, Date endAt) {
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.total_people = 0;
    }

}


