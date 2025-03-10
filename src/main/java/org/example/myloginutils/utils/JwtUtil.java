package org.example.myloginutils.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 密钥（至少 256 位）
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // JWT 过期时间（例如 1 小时）
    private static final long EXPIRATION_TIME = 3600_000; // 1 小时

    /**
     * 生成 JWT
     *
     * @param username 用户名
     * @return JWT 字符串
     */
    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)  // 设置主题（通常是用户名）
                .setIssuedAt(new Date())  // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))  // 设置过期时间
                .signWith(SECRET_KEY)  // 使用密钥签名
                .compact();  // 生成 JWT 字符串
    }
}