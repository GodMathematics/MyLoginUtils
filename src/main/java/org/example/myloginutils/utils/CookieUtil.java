package org.example.myloginutils.utils;

import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class CookieUtil {

    /**
     * 将 JWT 设置为 Cookie
     *
     * @param token JWT 字符串
     * @return 包含 Cookie 的 ResponseEntity
     */
    public static ResponseCookie setJwtCookie(String token) {
        // 创建 Cookie
        // Cookie 名称和值
        // 防止客户端脚本访问
        // 仅通过 HTTPS 传输
        // Cookie 的作用路径
        // Cookie 的有效期（秒）

        // 返回 ResponseEntity 并设置 Cookie
        return ResponseCookie.from("jwt", token)  // Cookie 名称和值
                .httpOnly(true)  // 防止客户端脚本访问
                .secure(true)    // 仅通过 HTTPS 传输
                .path("/")       // Cookie 的作用路径
                .maxAge(3600)    // Cookie 的有效期（秒）
                .build();
    }
}