package com.sascom.chickenstock.domain.competition.entity;


import com.sascom.chickenstock.domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import java.time.LocalDateTime;
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
    private LocalDateTime startAt;

    @NotNull
    @Column(name = "end_at")
    private LocalDateTime endAt;


    @OneToMany(mappedBy = "competition")
    private List<Account> accounts;

    @Column(name = "total_people")
    private Integer total_people;


    public Competition(String title, LocalDateTime startAt, LocalDateTime endAt) {
        this.title = title;
        this.startAt = startAt;
        this.endAt = endAt;
        this.total_people = 0;
    }

}


