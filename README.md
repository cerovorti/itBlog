# IT 技术博客与知识管理平台

面向计算机专业学生和 IT 从业者的全栈技术博客系统，聚焦 Markdown 创作、代码高亮、层级评论互动与 Web 安全防护，由 Spring Boot + Vue 3 前后端分离构建。

📁 [项目相关文档 (docs/)](./docs/)　🗄️ [数据库脚本 (backend/src/main/resources/db/)](./backend/src/main/resources/db/)

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.0 |
| ORM | MyBatis-Plus | 3.5.5 |
| 安全认证 | Spring Security + JWT (jjwt) | 0.12.3 |
| 数据库 | MySQL | 8.0+ |
| XSS 过滤 | Jsoup | 1.17.2 |
| 前端框架 | Vue 3 (Composition API) + TypeScript | 3.4 |
| 构建工具 | Vite | 5.4 |
| UI 组件库 | Element Plus | 2.5 |
| 状态管理 | Pinia | 2.1.7 |
| 路由 | Vue Router | 4.3 |
| HTTP 客户端 | Axios | 1.6 |
| Markdown 编辑器 | cherry-markdown | 0.11.0 |
| 代码高亮 | highlight.js | 11.9 |
| Markdown 解析 | markdown-it | 14.0 |

## 功能特性

### 创作者与读者端

- **门户首页**：按"最新发布""最多点赞""最多阅读"展示文章列表，侧边栏提供热门标签云、推荐作者、热门文章排行
- **Markdown 创作中心**：实时预览编辑器，支持代码块语法高亮、表格/图片快捷插入，粘贴或拖拽图片自动上传并回填 URL
- **文章阅读与互动**：Markdown 正文渲染为 HTML 并自动生成**文章目录大纲 (TOC)**，支持点赞、收藏
- **评论系统**：发表评论、回复他人，形成层级嵌套评论树
- **个人主页**：展示个人基本信息，分类查看"我发布的文章"与"我收藏的文章"
- **全文搜索**：文章关键词检索

### 管理后台

- **内容管理**：文章审核与下架、评论管理
- **专栏与标签管理**：维护一级/二级技术专栏（如 前端开发 → Vue.js），管理系统推荐的全局技术标签

### 安全防护

- **认证**：Spring Security + JWT 无状态令牌认证
- **XSS 防御**：Jsoup 对 Markdown 内容进行危险标签过滤，防止存储型 XSS 攻击

## 项目结构

```
├── backend/                              # 后端 Spring Boot 项目
│   ├── pom.xml                           # Maven 配置
│   ├── mvnw.cmd                          # Maven Wrapper (Windows)
│   └── src/main/
│       ├── java/com/itblog/
│       │   ├── ItBlogApplication.java    # 启动入口
│       │   ├── common/                   # 公共类 (统一响应、异常处理、分页)
│       │   ├── config/                   # 配置类 (MyBatis-Plus、WebMvc、XSS 过滤)
│       │   ├── controller/               # 控制器层
│       │   ├── dto/                      # 数据传输对象
│       │   ├── entity/                   # 数据库实体
│       │   ├── filter/                   # XSS 过滤器
│       │   ├── mapper/                   # MyBatis 数据访问层
│       │   ├── security/                 # Spring Security + JWT 安全配置
│       │   ├── service/                  # 服务接口
│       │   ├── service/impl/             # 服务实现
│       │   └── vo/                       # 视图对象
│       └── resources/
│           ├── application.yml           # 应用配置
│           └── db/                       # 数据库初始化脚本
├── frontend/                             # 前端 Vue 3 项目
│   ├── package.json                      # 依赖 & 脚本
│   ├── vite.config.ts                    # Vite 配置（含 API 代理）
│   ├── tsconfig.json                     # TypeScript 配置
│   ├── index.html                        # 入口 HTML
│   └── src/
│       ├── main.ts                       # 应用入口
│       ├── App.vue                       # 根组件
│       ├── api/                          # API 请求层
│       │   ├── request.ts                # Axios 拦截器封装
│       │   ├── types.ts                  # 通用类型定义
│       │   ├── admin.ts                  # 管理接口
│       │   ├── article.ts                # 文章接口
│       │   ├── auth.ts                   # 认证接口
│       │   ├── category.ts               # 分类接口
│       │   ├── comment.ts                # 评论接口
│       │   ├── favorite.ts               # 收藏接口
│       │   ├── like.ts                   # 点赞接口
│       │   ├── search.ts                 # 搜索接口
│       │   ├── tag.ts                    # 标签接口
│       │   ├── upload.ts                 # 上传接口
│       │   └── user.ts                   # 用户接口
│       ├── stores/                       # Pinia 状态管理
│       ├── router/                       # 路由配置
│       ├── components/                   # 公共组件
│       │   ├── article/                  # 文章相关子组件
│       │   ├── sidebar/                  # 侧边栏子组件
│       │   └── common/                   # 通用子组件
│       ├── composables/                  # 组合式函数
│       ├── styles/                       # 样式文件
│       ├── utils/                        # 工具函数
│       └── views/                        # 页面视图
│           └── admin/                    # 后台管理页面
├── docs/                                 # 项目文档
└── .gitignore                            # Git 忽略规则
```

## 数据库设计

系统使用 MySQL 8.0，核心数据表如下：

| 数据表 | 说明 | 核心字段 |
|--------|------|----------|
| `busi_article` | 文章主表 | `id`, `title`, `content` (longtext), `cover_img`, `author_id`, `category_id`, `view_count`, `like_count`, `status` |
| `busi_tag` | 标签字典表 | `id`, `name` (如 "Spring Boot") |
| `busi_article_tag` | 文章标签中间表 | `article_id`, `tag_id` (多对多关系) |
| `busi_comment` | 评论留言表 | `id`, `article_id`, `user_id`, `parent_id` (0 表示顶级评论), `reply_to_user_id`, `content` |
| `busi_like` | 点赞记录表 | `id`, `user_id`, `article_id` (同一用户只能点赞一次) |
| `busi_favorite` | 收藏记录表 | `id`, `user_id`, `article_id` |
| `busi_category` | 文章分类表 | `id`, `name`, `parent_id` (支持二级分类) |
| `sys_user` | 用户表 | `id`, `username`, `password`, `avatar`, `bio` |

> 初始化脚本位于 `backend/src/main/resources/db/` 目录。

## 核心业务流程

### 文章发布与 XSS 防御

```
用户撰写 Markdown → 粘贴/拖拽图片自动上传 → 提交原始 Markdown 至后端
→ Jsoup XSS 过滤器剔除危险标签 → 安全内容存入数据库
→ 读者访问时前端将 Markdown 解析为 HTML，高亮代码并生成目录大纲
```

### 评论树构建

数据库中以 `parent_id` 自关联存储扁平评论记录，后端查询后将扁平数据递归组装为带 `children` 数组的树状 JSON，前端渲染为层级嵌套评论。

## 接口概览 (RESTful API)

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/upload/image` | 图片上传，供 Markdown 编辑器使用 |
| `POST` | `/api/article/publish` | 发布文章 |
| `GET` | `/api/article/{id}` | 文章详情（阅读量 +1） |
| `PUT` | `/api/article/{id}` | 编辑文章 |
| `DELETE` | `/api/article/{id}` | 删除文章 |
| `GET` | `/api/comment/tree/{articleId}` | 获取文章评论树 |
| `POST` | `/api/comment` | 发表/回复评论 |
| `POST` | `/api/auth/login` | 用户登录 |
| `POST` | `/api/auth/register` | 用户注册 |
| `POST` | `/api/like/{articleId}` | 点赞文章 |
| `POST` | `/api/favorite/{articleId}` | 收藏文章 |
| `GET` | `/api/search` | 全文搜索 |

## 环境要求

- **JDK** 17+
- **Node.js** 18+
- **MySQL** 8.0+
- **Maven** 3.8+（可选，项目内置 Maven Wrapper）

## 快速开始

### 1. 克隆项目

```bash
# Gitee
git clone https://gitee.com/cerovorti/it-blog.git
# GitHub
git clone https://github.com/cerovorti/itBlog.git

cd it-blog
```

### 2. 初始化数据库

在 MySQL 中创建数据库并导入初始化脚本：

```bash
# 登录 MySQL 后执行
CREATE DATABASE IF NOT EXISTS demo_album DEFAULT CHARSET utf8mb4;
USE demo_album;
SOURCE backend/src/main/resources/db/demo_album_init.sql;
```

或直接导入：`backend/src/main/resources/db/demo_album_20260629_105013.sql`

### 3. 修改配置

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_album?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 4. 启动后端

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`

### 5. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器运行在 `http://localhost:5173`，API 请求会自动代理到后端 `http://localhost:8080`。

### 6. 访问

浏览器打开 `http://localhost:5173` 即可使用。

## Vite 代理配置

开发环境下，前端 `/api` 路径的请求会被代理到后端，无需手动处理跨域：

```typescript
// frontend/vite.config.ts
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 构建部署

### 后端构建

```bash
cd backend
./mvnw clean package -DskipTests
```

生成的 JAR 包位于 `backend/target/` 目录。

```bash
java -jar target/it-blog-backend-1.0.0.jar
```

### 前端构建

```bash
cd frontend
npm run build
```

生成的静态文件位于 `frontend/dist/` 目录，可直接部署到 Nginx 等 Web 服务器。