package com.acrtic.chat.utility;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {
    
    // private static SecretKey secretKey;

    // public JwtUtil(@Value("${secret.key}") String secret) {
    //     this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    // }


    private static final String SECRET =
            "this-is-a-very-strong-secret-key-for-jwt-signing-123456";



    private static final Key key = Keys.hmacShaKeyFor(
            SECRET.getBytes(StandardCharsets.UTF_8)
    );

    public static String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(key)
                .compact();
    }

    public static String validateToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

}
