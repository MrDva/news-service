package com.czb.news.controller;


import com.alipay.api.AlipayApiException;
import com.czb.news.entity.User;
import com.czb.news.service.PaymentService;
import com.czb.news.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

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
        User user = userService.findByUsername(auth.getName());
        try {
            return paymentService.createPayment(user, request.getAmount(), request.getPaymentMethod());
        } catch (IllegalStateException e) {
            logger.warn("Payment creation rejected: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * 处理支付宝支付回调（notifyUrl）
     *
     * @param outTradeNo  订单号
     * @param tradeStatus 交易状态
     */
    @PostMapping("/notify")
    public void handleNotify(@RequestParam("out_trade_no") String outTradeNo,
                             @RequestParam("trade_status") String tradeStatus) {
        // 调用 PaymentService 处理回调
        logger.info("Received Alipay callback: out_trade_no={}, trade_status={}", outTradeNo, tradeStatus);
        paymentService.handlePaymentCallback(outTradeNo, tradeStatus.equals("TRADE_SUCCESS") ? "success" : "failed");
        logger.info("Processed callback for order: {}", outTradeNo);
    }
}
