package com.czb.news.controller;


import com.alipay.api.AlipayApiException;
import com.czb.news.entity.User;
import com.czb.news.service.PaymentService;
import com.czb.news.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService; // 声明 UserService 字段

    public PaymentController(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService; // 注入 UserService
    }

    /**
     * 创建支付订单
     * @param auth 当前认证用户
     * @param request 支付请求（金额和支付方式）
     * @return 支付宝支付表单 HTML
     * @throws AlipayApiException 支付异常
     */
    @PostMapping("/create")
    public String createPayment(Authentication auth, @RequestBody PaymentRequest request) throws AlipayApiException {
        User user = userService.findByUsername(auth.getName()); // 使用注入的 userService
        return paymentService.createPayment(user, request.getAmount(), request.getPaymentMethod());
    }
}

/**
 * 支付请求 DTO
 */
class PaymentRequest {
    private double amount;
    private String paymentMethod;

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}