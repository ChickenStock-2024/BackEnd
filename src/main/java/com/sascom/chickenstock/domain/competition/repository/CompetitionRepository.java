package com.sascom.chickenstock.domain.competition.repository;

import com.sascom.chickenstock.domain.account.entity.Account;
import com.sascom.chickenstock.domain.competition.entity.Competition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    Competition findTopByAccountsOrderByIdDesc(Account account);

    Optional<Competition> findTopByOrderByIdDesc();
}
