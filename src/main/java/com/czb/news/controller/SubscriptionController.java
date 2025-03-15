package com.czb.news.controller;


import com.czb.news.entity.Subscription;
import com.czb.news.service.SubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscription")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // 处理订阅请求，需登录
    @PostMapping("/subscribe")
    public Subscription subscribe(Authentication authentication) {
        String username = authentication.getName(); // 从认证信息中获取当前用户名
        return subscriptionService.subscribeUser(username); // 创建订阅
    }

    // 检查当前用户的订阅状态
    @GetMapping("/status")
    public boolean checkSubscription(Authentication authentication) {
        String username = authentication.getName(); // 获取当前用户名
        return subscriptionService.isSubscribed(username); // 返回订阅状态
    }
}
