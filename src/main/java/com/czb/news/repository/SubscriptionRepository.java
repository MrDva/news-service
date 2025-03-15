package com.czb.news.repository;

import com.czb.news.entity.Subscription;
import com.czb.news.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    // 根据用户和订阅状态查询订阅记录
    Optional<Subscription> findByUserAndActive(User user, boolean active);
}