package com.czb.news.controller;

import com.czb.news.entity.News;
import com.czb.news.entity.User;
import com.czb.news.service.NewsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资讯控制器，处理资讯相关请求
 */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * 获取资讯列表
     * @param userName 当前认证用户
     * @return 资讯列表
     */
    @GetMapping
    public List<News> getNews(@AuthenticationPrincipal String userName) {
        return newsService.getNewsForUser(userName);
    }

    /**
     * 获取推荐资讯
     * @param userName 当前认证用户
     * @return 推荐资讯列表
     */
    @GetMapping("/recommend")
    public List<News> getRecommendedNews(@AuthenticationPrincipal String userName) {
        return newsService.recommendNews(userName);
    }

    /**
     * 获取资讯详情
     * @param id 资讯 ID
     * @param userName 当前认证用户
     * @return 资讯对象
     */
    @GetMapping("/{id}")
    public News getNewsById(@PathVariable Long id, @AuthenticationPrincipal String userName) {
        return newsService.getNewsById(id, userName);
    }
}
