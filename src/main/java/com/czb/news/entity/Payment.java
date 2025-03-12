package com.czb.news.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付记录实体类，对应数据库中的 payments 表
 */
@Entity
@Table(name = "payments",schema = "news_app")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键，自增

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 关联用户

    private double amount; // 支付金额

    private String paymentMethod; // 支付方式，如 "alipay" 或 "wechat"

    private String status; // 支付状态，如 "pending"、"success"、"failed"

    private LocalDateTime createdAt; // 创建时间
}
