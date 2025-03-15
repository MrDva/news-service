package com.czb.news.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.czb.news.entity.Payment;
import com.czb.news.entity.User;
import com.czb.news.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 支付服务类，处理支付逻辑（以支付宝为例）
 */
@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final SubscriptionService subscriptionService; // 新增 SubscriptionService 依赖

    @Value("${alipay.app-id}")
    private String appId;

    @Value("${alipay.private-key}")
    private String privateKey;

    @Value("${alipay.public-key}")
    private String publicKey;

    @Value("${alipay.gateway}")
    private String gateway;

    @Value("${alipay.notify-url}")
    private String notifyUrl;

    @Value("${alipay.return-url}")
    private String returnUrl;

    public PaymentService(PaymentRepository paymentRepository, SubscriptionService subscriptionService) {
        this.paymentRepository = paymentRepository;
        this.subscriptionService = subscriptionService; // 注入 SubscriptionService
    }

    /**
     * 创建支付订单
     * @param user 用户
     * @param amount 金额
     * @param paymentMethod 支付方式
     * @return 支付表单 HTML
     * @throws AlipayApiException 支付异常
     */
    public String createPayment(User user, double amount, String paymentMethod) throws AlipayApiException {
        AlipayClient alipayClient = new DefaultAlipayClient(gateway, appId, privateKey, "json", "UTF-8", publicKey, "RSA2");
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);
        String orderId = String.valueOf(System.currentTimeMillis());
        request.setBizContent("{\"out_trade_no\":\"" + orderId + "\"," +
                "\"total_amount\":\"" + amount + "\"," +
                "\"subject\":\"Premium Subscription\"," +
                "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setOrderId(orderId);
        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        logger.info("Saved payment with ID: {}, orderId: {}", payment.getId(), orderId);
        String form = alipayClient.pageExecute(request).getBody();
        logger.debug("Generated payment form: {}", form);
        return form;
    }

    /**
     * 处理支付回调，更新订阅状态
     * @param orderId 订单 ID
     * @param status 支付状态
     */
    @Transactional
    public void handlePaymentCallback(String orderId, String status) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(status);
        if ("success".equals(status)) {
            // 更新订阅状态，而不是 User.premium
            subscriptionService.subscribeUser(payment.getUser().getUsername());
        }
        paymentRepository.save(payment);
    }
}
