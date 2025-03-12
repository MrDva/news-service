package com.czb.news.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.czb.news.entity.Payment;
import com.czb.news.entity.User;
import com.czb.news.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 支付服务类，处理支付逻辑（以支付宝为例）
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;

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

    public PaymentService(PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
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
        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return alipayClient.pageExecute(request).getBody();
    }

    /**
     * 处理支付回调（示例）
     * @param orderId 订单 ID
     * @param status 支付状态
     */
    public void handlePaymentCallback(String orderId, String status) {
        Payment payment = paymentRepository.findById(Long.valueOf(orderId)).orElseThrow();
        payment.setStatus(status);
        if ("success".equals(status)) {
            userService.updatePremiumStatus(payment.getUser());
        }
        paymentRepository.save(payment);
    }
}
