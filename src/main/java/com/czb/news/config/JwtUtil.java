package com.czb.news.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // 密钥，至少 32 字节，用于 HS256 签名算法
    private static final String SECRET = "simple-secret-key-12345678901234567890";
    private final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes()); // 生成安全的密钥对象

    // token 有效期，1 小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * 生成 JWT token
     * @param username 用户名，作为 token 的主体
     * @return 生成的 JWT 字符串
     */
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)                   // 设置 token 主体（用户名）
                .setIssuedAt(new Date())                // 设置发行时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 设置过期时间
                .signWith(KEY, SignatureAlgorithm.HS256) // 使用 HS256 算法和密钥签名
                .compact();                             // 构建并返回 token
    }

    /**
     * 从 token 中提取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();           // 从 token 声明中提取用户名
    }

    /**
     * 验证 token 是否有效
     * @param token JWT token
     * @return true 表示有效，false 表示无效
     */
    public boolean validateToken(String token) {
        try {
            getClaims(token);                           // 解析 token，如果成功则有效
            return true;
        } catch (Exception e) {
            return false;                               // 解析失败（过期、签名错误等）
        }
    }

    /**
     * 解析 token 获取声明
     * @param token JWT token
     * @return Claims 对象，包含 token 的声明
     */
    private Claims getClaims(String token) {
        return Jwts.parser()                     // 使用新版 parserBuilder API
                .setSigningKey(KEY)                     // 设置签名密钥
                .build()                                // 构建解析器
                .parseClaimsJws(token)                  // 解析 token
                .getBody();                             // 返回声明
    }
}