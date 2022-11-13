package com.hufcusfocus.hufsland.config.auth;

import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//UserDetailsService는 IoC로 찾음
///loginProcess.do 가 찾아오는 클래스임.
@Service
@RequiredArgsConstructor
@Slf4j
public class CutomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("username = {}", email);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            List<GrantedAuthority> authorities = new ArrayList<>();
            User user = optionalUser.get();
            authorities.add(new SimpleGrantedAuthority(String.valueOf(user.getRole())));

            return new UserPrincipal(user);
        }
        return new UserPrincipal(null);
    }
}
