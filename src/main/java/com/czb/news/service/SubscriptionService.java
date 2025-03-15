package com.czb.news.service;


import com.czb.news.entity.Subscription;
import com.czb.news.entity.User;
import com.czb.news.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserService userService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
    }

    // 为用户创建订阅，设置一个月有效期
    public Subscription subscribeUser(String username) {
        User user = userService.findByUsername(username); // 获取用户信息
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setStartDate(LocalDateTime.now()); // 设置开始时间为当前时间
        subscription.setEndDate(LocalDateTime.now().plusMonths(1)); // 订阅有效期1个月
        subscription.setActive(true); // 标记为活跃状态
        return subscriptionRepository.save(subscription); // 保存到数据库
    }

    // 检查用户是否具有有效订阅
    public boolean isSubscribed(String username) {
        User user = userService.findByUsername(username);// 获取用户信息
        return subscriptionRepository.findByUserAndActive(user, true).isPresent(); // 检查是否存在活跃订阅
    }
}
