package com.czb.news.config;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AlipaySignatureFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AlipaySignatureFilter.class);

    @Value("${alipay.public-key}")
    private String alipayPublicKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getRequestURI().equals("/api/payment/notify")) {
            // 提取支付宝回调参数
            Map<String, String[]> paramMap = request.getParameterMap();
            Map<String, String> params = new HashMap<>();
            paramMap.forEach((key, value) -> params.put(key, value[0]));

            logger.info("Received Alipay callback: {}", params);

            try {
                // 验证签名
                boolean signVerified = AlipaySignature.rsaCheckV1(
                        params, alipayPublicKey, "UTF-8", "RSA2"
                );
                if (signVerified) {
                    logger.info("Alipay signature verified successfully");
                    request.setAttribute("alipaySignatureVerified", true);
                    filterChain.doFilter(request, response); // 签名合法，放行
                } else {
                    logger.warn("Alipay signature verification failed: {}", params);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("failure");
                }
            } catch (AlipayApiException e) {
                logger.error("Error verifying Alipay signature", e);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("failure");
            }
        } else {
            filterChain.doFilter(request, response); // 非回调请求继续处理
        }
    }

}