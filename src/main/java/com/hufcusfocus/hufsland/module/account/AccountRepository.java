package com.hufcusfocus.hufsland.module.account;

import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.domain.entity.account.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByEmailAndProvider(String email, Provider provider);
    Optional<Account> findByEmail(String email);
}
