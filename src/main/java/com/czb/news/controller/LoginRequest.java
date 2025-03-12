package com.czb.news.controller;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {
    private String username;                                    // 用户名
    private String password;                                    // 密码

}
