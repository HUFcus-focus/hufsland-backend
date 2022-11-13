package com.hufcusfocus.hufsland.module.auth;

import com.hufcusfocus.hufsland.domain.entity.auth.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AuthRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUserId(long userId);
}
