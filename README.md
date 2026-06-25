# IT 技术博客

一个基于 Spring Boot + Vue 3 的全栈技术博客系统，支持文章发布、Markdown 编辑、评论互动、收藏点赞等功能。

📁 [项目相关文件 (docs/)](./docs/)

🗄️ [数据库脚本 (backend/src/main/resources/db/)](./backend/src/main/resources/db/)

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.2.0 |
| ORM | MyBatis-Plus | 3.5.5 |
| 安全认证 | Spring Security + JWT (jjwt) | 0.12.3 |
| 数据库 | MySQL | — |
| XSS 过滤 | Jsoup | 1.17.2 |
| 前端框架 | Vue 3 + TypeScript | 3.4 |
| 构建工具 | Vite | 5.4 |
| UI 组件库 | Element Plus | 2.5 |
| 状态管理 | Pinia | 2.1.7 |
| 路由 | Vue Router | 4.3 |
| HTTP 客户端 | Axios | 1.6 |
| Markdown 编辑器 | cherry-markdown | 0.11.0 |
| 代码高亮 | highlight.js | 11.9 |

## 项目结构

```
├── backend/                     # 后端 Spring Boot 项目
│   ├── pom.xml                  # Maven 配置
│   ├── mvnw.cmd                 # Maven Wrapper (Windows)
│   └── src/
│       ├── main/java/com/itblog/ # Java 源代码
│       └── main/resources/      # 配置文件
├── frontend/                    # 前端 Vue 3 项目
│   ├── package.json             # 依赖 & 脚本
│   ├── vite.config.ts           # Vite 配置（含代理）
│   ├── tsconfig.json            # TypeScript 配置
│   ├── index.html               # 入口 HTML
│   └── src/
│       ├── main.ts              # 应用入口
│       ├── App.vue              # 根组件
│       ├── api/                 # API 请求层
│       │   ├── admin.ts         # 管理接口
│       │   ├── article.ts       # 文章接口
│       │   ├── auth.ts          # 认证接口
│       │   ├── category.ts      # 分类接口
│       │   ├── comment.ts       # 评论接口
│       │   ├── favorite.ts      # 收藏接口
│       │   ├── like.ts          # 点赞接口
│       │   ├── search.ts        # 搜索接口
│       │   ├── request.ts       # Axios 封装
│       │   ├── tag.ts           # 标签接口
│       │   ├── upload.ts        # 上传接口
│       │   └── user.ts          # 用户接口
│       ├── stores/              # Pinia 状态管理
│       ├── router/              # 路由配置
│       ├── components/          # 公共组件
│       ├── composables/         # 组合式函数
│       ├── styles/              # 样式文件
│       ├── utils/               # 工具函数
│       └── views/               # 页面视图
├── docs/                        # 文档目录
└── .gitignore                   # Git 忽略规则
```

## 功能特性

- **用户系统**：注册 / 登录 / 个人中心，JWT 令牌认证
- **文章管理**：发布 / 编辑 / 删除文章，支持 Markdown 编辑器
- **分类与标签**：文章分类及标签管理
- **评论互动**：文章评论功能
- **收藏点赞**：收藏文章、给文章点赞
- **全文搜索**：文章搜索功能
- **后台管理**：管理后台（用户、文章等管理）
- **安全防护**：Spring Security + JWT 认证、Jsoup XSS 过滤

## 环境要求

- **JDK** 17+
- **Node.js** 18+
- **MySQL** 8.0+
- **Maven** 3.8+（可选，项目内置 Maven Wrapper）

## 快速开始

### 1. 克隆项目

```bash
git clone <仓库地址>
cd 新建文件夹
```

### 2. 配置数据库

在 MySQL 中创建数据库，并修改后端配置文件（`backend/src/main/resources/application.yml` 或 `application.properties`）中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_album?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

### 3. 启动后端

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# macOS / Linux
./mvnw spring-boot:run
```

后端默认运行在 `http://localhost:8080`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器运行在 `http://localhost:5173`，API 请求会自动代理到后端 `http://localhost:8080`。

### 5. 访问

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

### 前端构建

```bash
cd frontend
npm run build
```

生成的静态文件位于 `frontend/dist/` 目录，可直接部署到 Nginx 等 Web 服务器。