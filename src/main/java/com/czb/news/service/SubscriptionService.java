package com.czb.news.service;


import com.czb.news.entity.Subscription;
import com.czb.news.entity.User;
import com.czb.news.repository.SubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SubscriptionService {
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionService.class);

    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserService userService) {
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
    }

    // 为用户创建订阅，设置一个月有效期
    public Subscription subscribeUser(String username) {
        User user = userService.findByUsername(username);
        // 检查是否已有有效订阅
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUserAndActiveTrueAndEndDateAfter(
                user, LocalDateTime.now()
        );
        if (existingSubscription.isPresent()) {
            logger.info("User {} already has an active subscription until {}", username, existingSubscription.get().getEndDate());
            return existingSubscription.get();
        }

        // 创建新订阅
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));
        subscription.setActive(true);
        subscriptionRepository.save(subscription);
        logger.info("Subscribed user: {} until {}", username, subscription.getEndDate());
        return subscription;
    }

    // 检查用户是否具有有效订阅
    public boolean isSubscribed(String username) {
        User user = userService.findByUsername(username);// 获取用户信息
        return subscriptionRepository.findByUserAndActive(user, true).isPresent(); // 检查是否存在活跃订阅
    }
}
