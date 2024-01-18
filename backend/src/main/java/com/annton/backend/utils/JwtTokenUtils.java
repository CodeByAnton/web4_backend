package com.annton.backend.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenUtils {
    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.lifetime}")
    private int lifetime;

    public String generateToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+lifetime))
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();

    }

    public String getUsername(String token){
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }



}
