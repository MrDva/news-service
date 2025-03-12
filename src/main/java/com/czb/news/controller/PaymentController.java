package com.czb.news.controller;

import com.alipay.api.AlipayApiException;
import com.czb.news.entity.User;
import com.czb.news.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器，处理支付相关请求
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 创建支付订单
     * @param user 当前认证用户
     * @return 支付表单 HTML
     * @throws AlipayApiException 支付异常
     */
    @PostMapping("/subscribe")
    public String createPayment(@AuthenticationPrincipal User user) throws AlipayApiException {
        return paymentService.createPayment(user, 10.0, "alipay");
    }

    /**
     * 处理支付回调（异步通知）
     * @param orderId 订单 ID
     * @param status 支付状态
     */
    @PostMapping("/notify")
    public void handlePaymentNotify(@RequestParam String orderId, @RequestParam String status) {
        paymentService.handlePaymentCallback(orderId, status);
    }
}
