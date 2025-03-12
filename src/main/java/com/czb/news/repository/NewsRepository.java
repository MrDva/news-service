package com.czb.news.repository;


import com.czb.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 资讯数据访问接口
 */
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByIsPremiumFalse(); // 查找免费资讯
    List<News> findByCategoryIn(List<String> categories); // 根据类别查找资讯
}
