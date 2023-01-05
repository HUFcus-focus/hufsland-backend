package com.hufcusfocus.hufsland.util;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;


@Component
@Slf4j
@AllArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.access-token.expire-length}")
    private final long ACCESS_TOKEN_VALIDITY;
    @Value("${jwt.refresh-token.expire-length}")
    private final long REFRESH_TOKEN_VALIDITY;
    @Value("${jwt.token.secret-key}")
    private final String SECRET_KEY;

    public String createAccessToken(String payload) {
        return createToken(payload, ACCESS_TOKEN_VALIDITY);
    }

    public String createRefreshToken() {
        byte[] array = new byte[7];
        new Random().nextBytes(array);
        String generatedString = new String(array, StandardCharsets.UTF_8);
        return createToken(generatedString, REFRESH_TOKEN_VALIDITY);
    }

    public String createToken(String payload, long expireLength) {
        Claims claims = Jwts.claims().setSubject(payload);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String getPayload(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        } catch (JwtException exception){
            log.warn("토큰정보 추출과정에서 예외 발생 = {}", exception.getMessage());
            throw new RuntimeException("유효하지 않은 토큰 입니다"); //TODO : 예외처리 어떻게?
        }
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (SignatureException exception) {
            log.warn("토큰검증 과정에서 예외 발생 = {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("토큰검증 과정에서 예외 발생 = {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.warn("토큰검증 과정에서 예외 발생 = {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("토큰검증 과정에서 예외 발생 = {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("토큰검증 과정에서 예외 발생 = {}", exception.getMessage());
        } catch (NullPointerException exception){
            log.warn("토큰검증 과정에서 예외 발생 = {}", exception.getMessage());
        }
        return false;
    }
}