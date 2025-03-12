package com.czb.news.repository;


import com.czb.news.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问接口，继承 JpaRepository 提供 CRUD 操作
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // 根据用户名查找用户
}
