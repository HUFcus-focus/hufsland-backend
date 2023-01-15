package com.hufcusfocus.hufsland.filter;

import com.hufcusfocus.hufsland.config.auth.AccountPrincipal;
import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.module.account.AccountRepository;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private AccountRepository accountRepository;
    private JwtTokenProvider jwtTokenProvider;
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String HEADER_AUTHORIZATION_PREFIX = "Bearer ";
    private final String EXCEPTION_URI = "/v1/auth";

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, AccountRepository accountRepository, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.accountRepository = accountRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        if (!requestURI.contains(EXCEPTION_URI)) { //TODO : 예외 URI 로직 뺴보기
            String accessToken = request.getHeader(HEADER_AUTHORIZATION).replace(HEADER_AUTHORIZATION_PREFIX, "");

            String accountId = jwtTokenProvider.getPayload(accessToken);
            Optional<Account> optionalAccount = accountRepository.findById(Integer.parseInt(accountId));
            if (optionalAccount.isEmpty()) {
                throw new RuntimeException("잘못된 접근입니다.");
            }

            AccountPrincipal accountPrincipal = new AccountPrincipal(optionalAccount.get());
            Authentication authentication = new UsernamePasswordAuthenticationToken(accountPrincipal,
                    null, accountPrincipal.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
