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
public class CustomAccountDetailService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findByEmail(email);
        try {
            Account account = optionalAccount.get();
            return new AccountPrincipal(account);
        } catch (NullPointerException exception) {
            log.warn("존재하지 않는 이메일 입니다.");
        }
        return null;
    }
}
