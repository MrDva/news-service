package com.czb.news.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 用户实体类，对应数据库中的 users 表
 */
@Entity
@Table(name = "users",schema = "news_app")
@Data
public class User  implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 主键，自增

    @Column(unique = true, nullable = false)
    private String username; // 用户名，唯一且不可为空

    @Column(nullable = false)
    private String password; // 密码，使用 BCrypt 加密存储

    private String email; // 邮箱，可为空


    @Column(columnDefinition = "json")
    @Type(value = JsonType.class)
    private String preferences; // 用户偏好，JSON 格式存储，如 ["科技", "体育"]

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}