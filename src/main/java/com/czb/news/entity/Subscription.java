package com.czb.news.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "subscriptions")
public class Subscription {
    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 订阅记录的唯一标识

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 关联的用户

    private LocalDateTime startDate; // 订阅开始时间
    private LocalDateTime endDate;   // 订阅结束时间
    private boolean active;          // 订阅是否有效

}