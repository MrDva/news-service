# 资讯订阅应用需求文档

## 1. 项目概述

### 1.1 项目目标
开发一个资讯订阅服务应用，用户可以通过该应用查阅资讯、登录认证、获取个性化推荐，并通过付费订阅增值服务。应用需具备清晰的代码结构，易于扩展，支持前后端分离。

### 1.2 功能需求
1. **资讯查阅**：
    - 用户可以浏览资讯列表和查看详情。
    - 支持免费和付费资讯区分。
2. **用户登录鉴权**：
    - 用户通过用户名和密码登录，获取访问权限。
    - 使用 JWT（JSON Web Token）进行身份认证。
3. **个性化资讯推荐**：
    - 根据用户偏好（如科技、体育）推荐相关资讯。
4. **数据库存储**：
    - 资讯和用户信息存储在 PostgreSQL 数据库中。
5. **增值服务**：
    - 用户可以通过微信或支付宝付费订阅，解锁付费资讯。

---

## 2. 技术架构

### 2.1 技术选型
- **前端**：
    - **框架**：React（使用 Hooks 和 Context API）
    - **语言**：JavaScript/TypeScript
- **后端**：
    - **框架**：Spring Boot 3.2.3
    - **语言**：Java 17
    - **安全**：Spring Security（JWT 鉴权）
    - **数据访问**：Spring Data JPA（Hibernate）
- **数据库**：
    - **类型**：PostgreSQL 15
    - **Schema**：`news.news_app`
- **第三方服务**：
    - **支付**：微信支付 SDK / 支付宝 SDK（待集成）
- **测试工具**：
    - **API 测试**：Bruno（支持 Postman v2.1 格式）
    - **单元测试**：JUnit 5（通过 `spring-boot-starter-test`）

### 2.2 系统架构图
+----------------+        +----------------+        +----------------+
|   React UI     | <----> | Spring Boot API| <----> | PostgreSQL     |
| (Frontend)     |  HTTP  | (Backend)      |  JDBC  | (Database)     |
+----------------+        +----------------+        +----------------+
                            |       ^
                            v       |
                        +----------------+
                        | Payment SDK    |
                        | (WeChat/Alipay)|
                        +----------------+

- **前端**：通过 HTTP 请求调用后端 API。
- **后端**：处理业务逻辑，访问数据库，提供 RESTful API。
- **数据库**：存储用户和资讯数据。
- **支付服务**：通过 SDK 与第三方支付平台交互。

---

## 3. 设计流程

### 3.1 用户流程
1. **登录**：
    - 用户输入用户名和密码，发送至 `/api/auth/login`。
    - 后端验证并返回 JWT。
2. **资讯查阅**：
    - 用户访问 `/api/news` 获取资讯列表。
    - 点击某条资讯，访问 `/api/news/{id}` 查看详情。
3. **个性化推荐**：
    - 用户设置偏好（如 `"tech", "sports"`），存储在 `users.preferences`。
    - 后端根据偏好筛选资讯。
4. **增值服务订阅**：
    - 用户访问 `/api/payment/subscribe`，提交支付请求。
    - 后端调用支付 SDK，返回支付链接。
    - 用户完成支付，更新 `is_premium` 状态。

### 3.2 数据流程
- **登录**：
    - 请求 -> 认证控制器 -> 认证管理器 -> 用户服务 -> `users` 表 -> 返回 JWT。
- **资讯查阅**：
    - 请求 -> 资讯控制器 -> 资讯服务 -> 资讯仓库 -> `news` 表 -> 返回数据。
- **推荐**：
    - 用户偏好 -> 资讯服务 -> 筛选 `news` 表 -> 返回推荐列表。
- **支付**：
    - 请求 -> 支付控制器 -> 支付服务 -> 支付 SDK -> 更新 `users` 表。

---

## 4. 后端代码框架

### 4.1 目录结构
src/main/java/com/example/news_subscription/
├── config/                  # 配置类
│   ├── JwtUtil             # JWT 生成与验证工具
│   ├── JwtAuthenticationFilter # JWT 过滤器
│   └── SecurityConfig      # Spring Security 配置
├── controller/             # 控制器层
│   ├── AuthController      # 认证接口
│   ├── NewsController      # 资讯接口
│   └── PaymentController   # 支付接口
├── entity/                 # 实体层
│   ├── User                # 用户实体
│   └── News                # 资讯实体
├── repository/             # 数据访问层
│   ├── UserRepository      # 用户数据访问
│   └── NewsRepository      # 资讯数据访问
├── service/                # 服务层
│   ├── CustomUserDetailsService # 用户认证服务
│   ├── NewsService         # 资讯业务逻辑
│   └── PaymentService      # 支付业务逻辑（待完善）
└── NewsSubscriptionApplication # 主类


### 4.2 配置文件
- **文件**：`application.yml`
- **内容**：
    - 配置数据库连接（`jdbc:postgresql://localhost:5432/news?currentSchema=news_app`）。
    - 设置 JPA 属性（默认 schema 为 `news_app`，Hibernate 自动更新表结构）。

---

## 5. 数据库框架

### 5.1 表结构
- **Schema**：`news_app`
- **表**：
    1. **`users`**（用户信息）
        - `id` (bigint, 主键, 自增)
        - `username` (varchar, 唯一)
        - `password` (varchar, BCrypt 加密)
        - `email` (varchar)
        - `is_premium` (boolean, 是否付费用户)
        - `preferences` (json, 用户偏好，如 `["tech", "sports"]`)
    2. **`news`**（资讯表）
        - `id` (bigint, 主键, 自增)
        - `title` (varchar)
        - `content` (text)
        - `category` (varchar, 如 "tech", "sports")
        - `is_premium` (boolean, 是否付费内容)
    3. **`payments`**（支付记录，待实现）
        - `id` (bigint, 主键, 自增)
        - `user_id` (bigint, 外键关联 `users`)
        - `amount` (decimal)
        - `payment_method` (varchar, "wechat" 或 "alipay")
        - `status` (varchar, "pending", "success", "failed")
        - `created_at` (timestamp)

### 5.2 SQL 初始化
- 创建 `news_app` schema。
- 定义 `users`、`news` 和 `payments` 表，支持外键关系。

---

## 6. 待实现功能

### 6.1 个性化推荐
- **目标**：根据用户偏好返回匹配的资讯。
- **实现方式**：
    - 在资讯服务中基于用户偏好筛选资讯。
    - 更新资讯控制器以调用推荐逻辑。

### 6.2 支付集成
- **目标**：支持微信/支付宝支付。
- **实现方式**：
    - 引入支付 SDK（如支付宝 SDK）。
    - 在支付服务中实现支付请求和回调处理。

### 6.3 前端实现
- **目标**：提供用户界面。
- **实现方式**：
    - 使用 React 实现登录、资讯列表和支付页面。
    - 通过 HTTP 调用后端 API。

---

## 7. 测试计划
- **单元测试**：
    - 测试资讯服务和支付服务的核心逻辑。
- **API 测试**：
    - 使用 Bruno 导入 Postman v2.1 集合，验证所有接口。
- **集成测试**：
    - 验证前端与后端的交互。

---

## 8. 部署与优化
- **部署**：
    - 使用 Docker 容器化应用。
    - 配置 PostgreSQL 云服务。
- **优化**：
    - 添加日志记录。
    - 实现全局异常处理。

---

## 9. 总结
本需求文档定义了资讯订阅应用的完整功能和技术实现框架。目前后端基础功能已就绪，数据库配置完成，测试接口可用。下一步需聚焦于推荐逻辑、支付集成和前端开发，以实现所有需求。文档将作为开发和维护的指导依据，确保项目按计划推进。
