package com.czb.news.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 资讯实体类，对应数据库中的 news 表
 */
@Entity
@Table(name = "news" ,schema = "news_app")
@Data
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键，自增

    @Column(nullable = false)
    private String title; // 资讯标题

    @Column(columnDefinition = "text")
    private String content; // 资讯内容

    private String category; // 资讯类别，如 "科技"、"体育"

    private LocalDateTime createdAt; // 创建时间

    private boolean isPremium; // 是否为付费内容
}
