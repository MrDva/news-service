package com.czb.news.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;                      // JWT 工具类实例

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 过滤器核心方法，处理每个请求的 JWT 验证
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization"); // 从请求头获取 Authorization

        // 检查 header 是否包含 Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);         // 提取 token，去掉 "Bearer " 前缀
            String username = jwtUtil.extractUsername(token); // 从 token 中解析用户名


            // 如果 token 有效且当前未认证，则设置认证信息
            if (username != null && jwtUtil.validateToken(token) &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(username, null, null); // 创建认证对象，无权限列表
                SecurityContextHolder.getContext().setAuthentication(auth); // 设置到 Security 上下文
            }
        }
        filterChain.doFilter(request, response);            // 继续处理请求
    }
}