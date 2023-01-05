package com.hufcusfocus.hufsland.module.user;

import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.domain.entity.account.Provider;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<Account> findByEmailAndProvider(String email, Provider provider);
}
