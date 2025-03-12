package com.czb.news.service;

import com.czb.news.entity.News;
import com.czb.news.entity.User;
import com.czb.news.repository.NewsRepository;
import com.czb.news.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 资讯服务类，处理资讯查询和推荐逻辑
 */
@Service
public class NewsService {

    private final NewsRepository newsRepository;

    private final UserRepository userRepository;

    public NewsService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取用户可查看的资讯列表
     * @param userName 用户
     * @return 资讯列表
     */
    public List<News> getNewsForUser(String userName) {
        if (getUser(userName).isPremium()) {
            return newsRepository.findAll();
        }
        return newsRepository.findByIsPremiumFalse();
    }

    /**
     * 根据用户偏好推荐资讯
     * @param userName 用户
     * @return 推荐资讯列表
     */
    public List<News> recommendNews(String userName) {
        List<String> preferences = Arrays.asList(getUser(userName).getPreferences().split(","));
        return newsRepository.findByCategoryIn(preferences);
    }

    /**
     * 获取单条资讯详情
     * @param id 资讯 ID
     * @param userName 用户
     * @return 资讯对象
     */
    public News getNewsById(Long id, String userName) {
        News news = newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
        if (news.isPremium() && !getUser(userName).isPremium()) {
            throw new AccessDeniedException("Premium content requires subscription");
        }
        return news;
    }

    public User getUser(String userName){
        Optional<User> optionalUser = userRepository.findByUsername(userName);
        return optionalUser.orElse(new User());
    }

}