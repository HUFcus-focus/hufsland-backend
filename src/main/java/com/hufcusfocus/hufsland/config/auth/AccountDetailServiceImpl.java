package com.hufcusfocus.hufsland.config.auth;

import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.module.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountDetailServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        if (optionalAccount.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 이메일 입니다."); //TODO : 이메일 존재 X 예외처리
        }
        return new AccountPrincipal(optionalAccount.get());
    }
}
