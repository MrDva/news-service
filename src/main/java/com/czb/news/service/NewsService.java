package com.czb.news.service;

import com.czb.news.entity.News;
import com.czb.news.entity.User;
import com.czb.news.repository.NewsRepository;
import com.czb.news.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 资讯服务类，处理资讯查询和推荐逻辑
 */
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    private final UserRepository userRepository;

    private final SubscriptionService subscriptionService;

    public NewsService(NewsRepository newsRepository, UserRepository userRepository, SubscriptionService subscriptionService) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;
    }

    /**
     * 获取用户可查看的资讯列表
     * @param userName 用户
     * @return 资讯列表
     */
    public List<News> getNewsForUser(String userName) {
        if (subscriptionService.isSubscribed(userName)) {
            return newsRepository.findAll();
        }
        return newsRepository.findByPremiumFalse();
    }

    /**
     * 根据用户偏好推荐资讯，根据订阅状态过滤
     * @param userName 用户名
     * @return 推荐资讯列表，已订阅用户可包含付费内容，未订阅用户只包含免费内容
     */
    public List<News> recommendNews(String userName) {
        boolean isSubscribed = subscriptionService.isSubscribed(userName); // 检查订阅状态
        List<String> preferences = Arrays.asList(getUser(userName).getPreferences().split(",")); // 获取用户偏好
        List<News> recommendedNews = newsRepository.findByCategoryIn(preferences); // 根据偏好查询
        if (!isSubscribed) {
            // 未订阅用户过滤掉付费内容
            return recommendedNews.stream()
                    .filter(news -> !news.isPremium())
                    .collect(Collectors.toList());
        }
        return recommendedNews; // 已订阅用户返回完整推荐列表
    }

    /**
     * 获取单条资讯详情
     * @param id 资讯 ID
     * @param userName 用户
     * @return 资讯对象
     */
    public News getNewsById(Long id, String userName) {
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        if (news.isPremium() && !subscriptionService.isSubscribed(userName)) {
            throw new AccessDeniedException("Premium content requires subscription");
        }
        return news;
    }

    /**
     * 获取用户信息，辅助方法
     *
     * @param userName 用户名
     * @return 用户对象，如果不存在返回空用户
     */
    public User getUser(String userName){
        Optional<User> optionalUser = userRepository.findByUsername(userName);
        return optionalUser.orElse(new User());
    }

}