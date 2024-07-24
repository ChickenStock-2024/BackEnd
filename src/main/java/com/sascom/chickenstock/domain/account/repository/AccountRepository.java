package com.sascom.chickenstock.domain.account.repository;

import com.sascom.chickenstock.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
