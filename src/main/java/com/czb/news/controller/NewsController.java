package com.czb.news.controller;

import com.czb.news.entity.News;
import com.czb.news.service.NewsService;
import com.czb.news.service.SubscriptionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 资讯控制器，处理资讯相关请求
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final SubscriptionService subscriptionService; // 新增订阅服务依赖

    public NewsController(NewsService newsService, SubscriptionService subscriptionService) {
        this.newsService = newsService;
        this.subscriptionService = subscriptionService; // 注入 SubscriptionService
    }

    /**
     * 获取资讯列表，根据订阅状态过滤付费内容
     *
     * @param userName 当前认证用户
     * @return 资讯列表，未订阅用户只能看到免费内容
     */
    @GetMapping
    public List<News> getNews(@AuthenticationPrincipal String userName) {
        boolean isSubscribed = subscriptionService.isSubscribed(userName); // 检查订阅状态
        List<News> newsList = newsService.getNewsForUser(userName); // 获取用户可看到的资讯
        if (!isSubscribed) {
            // 未订阅用户过滤掉付费内容
            return newsList.stream().filter(news -> !news.isPremium()) // 假设 News 实体有 isPremium 方法
                    .collect(Collectors.toList());
        }
        return newsList; // 已订阅用户返回完整列表
    }

    /**
     * 获取推荐资讯，根据订阅状态可能包含付费内容
     *
     * @param userName 当前认证用户
     * @return 推荐资讯列表
     */
    @GetMapping("/recommend")
    public List<News> getRecommendedNews(@AuthenticationPrincipal String userName) {
        boolean isSubscribed = subscriptionService.isSubscribed(userName); // 检查订阅状态
        List<News> recommendedNews = newsService.recommendNews(userName); // 获取推荐资讯
        if (!isSubscribed) {
            // 未订阅用户过滤掉付费内容
            return recommendedNews.stream().filter(news -> !news.isPremium()).collect(Collectors.toList());
        }
        return recommendedNews; // 已订阅用户返回完整推荐列表
    }

    /**
     * 获取资讯详情，检查订阅状态以限制付费内容访问
     *
     * @param id       资讯 ID
     * @param userName 当前认证用户
     * @return 资讯对象，如果是付费内容且未订阅则抛出异常
     */
    @GetMapping("/{id}")
    public News getNewsById(@PathVariable Long id, @AuthenticationPrincipal String userName) {
        boolean isSubscribed = subscriptionService.isSubscribed(userName); // 检查订阅状态
        News news = newsService.getNewsById(id, userName); // 获取资讯详情
        if (news.isPremium() && !isSubscribed) {
            throw new RuntimeException("Premium content requires an active subscription"); // 限制访问
        }
        return news;
    }
}