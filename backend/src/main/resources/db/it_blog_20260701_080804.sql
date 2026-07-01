-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: demo_album
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `busi_article`
--

DROP TABLE IF EXISTS `busi_article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始Markdown正文',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '封面图URL',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint NOT NULL COMMENT '所属分类ID',
  `view_count` int DEFAULT '0' COMMENT '阅读量',
  `like_count` int DEFAULT '0' COMMENT '点赞量',
  `comment_count` int DEFAULT '0' COMMENT '评论数',
  `status` tinyint DEFAULT '0' COMMENT '0=草稿 1=已发布',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `summary` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文章摘要',
  `is_deleted` tinyint DEFAULT '0' COMMENT '软删除',
  `draft_content` longtext COMMENT '待审核的新内容',
  `draft_summary` varchar(500) DEFAULT NULL COMMENT '待审核的新摘要',
  `review_status` int NOT NULL DEFAULT '0' COMMENT '0=无，1=有待审核新版本',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_author` (`author_id`) USING BTREE,
  KEY `idx_category` (`category_id`) USING BTREE,
  KEY `idx_status_create` (`status`,`create_time`) USING BTREE,
  KEY `idx_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文章主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_article`
--

LOCK TABLES `busi_article` WRITE;
/*!40000 ALTER TABLE `busi_article` DISABLE KEYS */;
INSERT INTO `busi_article` VALUES (1,'Updated','# UPD','',1,1,58,1,14,1,'2026-06-22 11:50:44','2026-06-26 22:47:06','',0,NULL,NULL,0),(3,'java','## cesi ![image](/uploads/images/cbb822c3-f3c7-4952-b8e5-286eac690ac5.png)','https://lf3-starry.byteimg.com/obj/starry/image/9kwkejo90a_1.webp',1,2,3,1,0,1,'2026-06-22 13:44:07','2026-06-22 17:03:44','',1,NULL,NULL,0),(4,'Test Draft','# Draft content','',1,1,0,0,0,0,'2026-06-22 13:47:26','2026-06-22 16:52:09','',0,NULL,NULL,0),(10,'Spring Boot 3 快速入门指南','# Spring Boot 3 快速入门\n\nSpring Boot 是快速开发脚手架。\n\n```java\n@SpringBootApplication\npublic class DemoApplication { }\n```','',1,2,130,15,2,1,'2026-06-22 15:51:23','2026-06-26 20:33:32','面向初学者的 Spring Boot 3 入门',0,NULL,NULL,0),(11,'MySQL 索引优化实践','# MySQL 索引优化\n\n良好的索引设计可以大幅提升查询性能。\n\n常见优化策略：最左前缀原则、覆盖索引、避免索引失效。\n\n```sql\nEXPLAIN SELECT * FROM users WHERE username = admin;\n```','',12,2,90,10,1,1,'2026-06-22 15:51:48','2026-06-29 20:13:03','记录一次 MySQL 索引优化的实际经验',0,NULL,NULL,0),(12,'Docker 容器化部署','# Docker 部署指南\n\n使用 Docker 可以快速部署应用，告别环境不一致的烦恼。\n\n```dockerfile\nFROM openjdk:17\nCOPY target/app.jar app.jar\nENTRYPOINT [java, -jar, /app.jar]\n```\n\nDocker Compose 可以编排多容器应用。','',13,3,216,23,2,1,'2026-06-22 15:51:52','2026-06-26 22:56:59','从零开始学习 Docker 容器化部署',0,NULL,NULL,0),(13,'Vue 3 Composition API 实战','# Vue 3 Composition API 实战\n\nComposition API 的核心是 setup 函数。\n\n## 基本用法\n```vue\n<script setup>\nimport { ref } from \'vue\'\nconst count = ref(0)\n</script>\n```\n\n让逻辑复用变得更简单。','',1,1,283,28,7,1,'2026-06-22 15:52:19','2026-06-26 20:32:59','分享 Vue 3 Composition API 的实用技巧',0,NULL,NULL,0),(14,'Python 数据分析入门','# Python 数据分析入门\n\n## Pandas 基础\n```python\nimport pandas as pd\ndf = pd.read_csv(\'data.csv\')\nprint(df.head())\n```\n\nPandas 是数据分析的核心工具。','',12,4,204,20,0,1,'2026-06-22 15:52:19','2026-06-26 18:05:57','Python Pandas 数据分析入门教程',0,NULL,NULL,0),(15,'Spring Boot 3 快速入门指南','# Spring Boot 3 快速入门\n\nSpring Boot 是 Java 生态中最流行的快速开发框架。\n\n## 核心特性\n- 自动配置：根据依赖自动配置 Bean\n- 起步依赖：一站式引入常用组件\n- 内嵌服务器：无需部署 Tomcat\n\n## 快速开始\n```java\n@SpringBootApplication\npublic class DemoApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(DemoApplication.class, args);\n    }\n}\n```\n\n只需一个注解即可启动应用。','https://picsum.photos/seed/spring/800/400',1,2,140,15,2,1,'2026-06-22 16:53:06','2026-06-26 20:32:56','面向初学者的 Spring Boot 3 入门教程，涵盖核心特性和快速上手',0,NULL,NULL,0),(16,'Vue 3 Composition API 实战技巧','# Vue 3 Composition API 实战\n\nComposition API 是 Vue 3 最重要的新特性。\n\n## setup 语法糖\n```vue\n<script setup>\nimport { ref, computed } from \'vue\'\nconst count = ref(0)\nconst double = computed(() => count.value * 2)\n</script>\n```\n\n## 优势\n- 更好的逻辑复用\n- 更清晰的代码组织\n- 完整的 TypeScript 支持','https://picsum.photos/seed/vue/800/400',1,1,256,28,2,0,'2026-06-29 10:22:05','2026-06-29 10:22:15','分享 Vue 3 Composition API 的实用技巧和最佳实践',0,NULL,NULL,0),(17,'MySQL 索引优化实践','# MySQL 索引优化实践\n\n良好的索引设计可以大幅提升查询性能。\n\n## 常见优化策略\n1. 最左前缀原则\n2. 覆盖索引减少回表\n3. 避免在索引列上使用函数\n\n## 使用 EXPLAIN 分析\n```sql\nEXPLAIN SELECT * FROM users WHERE username = \'admin\';\n```\n\n通过执行计划判断索引是否生效。','https://picsum.photos/seed/mysql/800/400',12,2,86,10,1,1,'2026-06-22 16:53:06','2026-06-22 17:03:44','记录一次 MySQL 索引优化的实际经验，含 EXPLAIN 分析技巧',0,NULL,NULL,0),(18,'Docker 容器化部署全流程','# Docker 容器化部署\n\n使用 Docker 可以告别\"在我机器上能跑\"的问题。\n\n## Dockerfile 示例\n```dockerfile\nFROM openjdk:17\nCOPY target/app.jar app.jar\nENTRYPOINT [\"java\", \"-jar\", \"/app.jar\"]\n```\n\n## Docker Compose\n```yaml\nversion: \"3\"\nservices:\n  app:\n    build: .\n    ports:\n      - \"8080:8080\"\n```','https://picsum.photos/seed/docker/800/400',13,3,198,22,2,1,'2026-06-22 16:53:06','2026-06-26 22:57:06','从零开始学习 Docker 容器化部署的完整流程',0,NULL,NULL,0),(19,'Python 数据分析入门','# Python 数据分析入门\n\nPandas 是 Python 数据分析的核心库。\n\n## 基本操作\n```python\nimport pandas as pd\ndf = pd.read_csv(\"data.csv\")\nprint(df.describe())\n```\n\n## 数据可视化\n```python\nimport matplotlib.pyplot as plt\ndf.plot(kind=\"bar\")\nplt.show()\n```','https://picsum.photos/seed/python/800/400',12,4,199,20,0,1,'2026-06-22 16:53:06','2026-06-29 20:13:08','Python Pandas 数据分析入门教程，面向数据科学初学者',0,NULL,NULL,0),(20,'示例技术文档：RESTful API 设计指南','# 示例技术文档：RESTful API 设计指南\n\n**版本**：v2.1.0  \n**状态**：草稿  \n**最后更新**：2026-06-29  \n\n---\n\n## 1. 概述\n\n本文档定义了 **Awesome API** 的设计规范、端点约定、身份验证机制及错误处理策略，旨在为内部开发者和第三方集成提供一致、可预测的接口体验。\n\n### 1.1 目标\n\n- 提供资源导向的 REST 接口\n- 使用 JSON 作为主要数据交换格式\n- 支持版本控制与平滑升级\n- 提供清晰的错误反馈\n\n### 1.2 非目标\n\n- 不涉及前端 UI 或客户端缓存策略\n- 不涵盖内部服务间异步通信（使用消息队列）\n\n---\n\n## 2. 快速开始\n\n### 2.1 基础 URL\n\n```plaintext\nhttps://api.example.com/v2\n```\n\n### 2.2 身份验证\n\n所有请求必须携带 `X-API-Key` 头部：\n\n```bash\ncurl -X GET \"https://api.example.com/v2/users\" \\\n  -H \"X-API-Key: your_api_key_here\"\n```\n\n> **注意**：生产环境请使用 OAuth 2.0 Bearer Token，开发环境可使用静态 Key。\n\n---\n\n## 3. 通用约定\n\n### 3.1 HTTP 方法\n\n| 方法   | 用途               | 幂等 |\n|--------|-------------------|------|\n| GET    | 获取资源           | 是   |\n| POST   | 创建资源           | 否   |\n| PUT    | 全量更新资源       | 是   |\n| PATCH  | 部分更新资源       | 否   |\n| DELETE | 删除资源           | 是   |\n\n### 3.2 状态码\n\n- `200 OK` – 请求成功\n- `201 Created` – 资源创建成功\n- `400 Bad Request` – 请求参数错误\n- `401 Unauthorized` – 未认证\n- `403 Forbidden` – 无权限\n- `404 Not Found` – 资源不存在\n- `429 Too Many Requests` – 速率限制\n- `500 Internal Server Error` – 服务端错误\n\n### 3.3 分页\n\nGET 集合资源时，支持以下查询参数：\n\n```http\nGET /users?page=2&limit=20\n```\n\n响应示例：\n\n```json\n{\n  \"data\": [ ... ],\n  \"pagination\": {\n    \"current_page\": 2,\n    \"total_pages\": 5,\n    \"total_count\": 98,\n    \"per_page\": 20\n  }\n}\n```\n\n---\n\n## 4. 核心资源 API\n\n### 4.1 用户（User）\n\n#### 获取用户列表\n\n```http\nGET /users\n```\n\n**查询参数**：\n\n| 参数 | 类型 | 说明 |\n|------|------|------|\n| `role` | string | 过滤角色 (`admin`, `editor`, `viewer`) |\n| `active` | boolean | 是否启用 |\n| `sort` | string | 排序字段，例如 `created_at:desc` |\n\n**响应示例**：\n\n```json\n{\n  \"data\": [\n    {\n      \"id\": \"usr_123\",\n      \"email\": \"alice@example.com\",\n      \"full_name\": \"Alice Chen\",\n      \"role\": \"admin\",\n      \"created_at\": \"2026-06-01T10:00:00Z\"\n    }\n  ],\n  \"pagination\": { ... }\n}\n```\n\n#### 创建用户\n\n```http\nPOST /users\n```\n\n**请求体**：\n\n```json\n{\n  \"email\": \"bob@example.com\",\n  \"full_name\": \"Bob Wang\",\n  \"password\": \"secureP@ssw0rd\",\n  \"role\": \"editor\"\n}\n```\n\n**成功响应**（201）：\n\n```json\n{\n  \"id\": \"usr_456\",\n  \"email\": \"bob@example.com\",\n  \"full_name\": \"Bob Wang\",\n  \"role\": \"editor\",\n  \"created_at\": \"2026-06-29T08:30:00Z\"\n}\n```\n\n### 4.2 项目（Project）\n\n#### 获取单个项目\n\n```http\nGET /projects/{project_id}\n```\n\n**路径参数**：\n\n- `project_id` (string, required) – 项目唯一标识符\n\n**响应示例**：\n\n```json\n{\n  \"id\": \"prj_789\",\n  \"name\": \"AI 推理平台\",\n  \"description\": \"基于 Transformer 的实时推理服务\",\n  \"owner_id\": \"usr_123\",\n  \"members\": [\n    { \"user_id\": \"usr_123\", \"role\": \"owner\" },\n    { \"user_id\": \"usr_456\", \"role\": \"contributor\" }\n  ],\n  \"status\": \"active\",\n  \"created_at\": \"2026-06-15T09:00:00Z\"\n}\n```\n\n---\n\n## 5. 错误处理\n\n所有错误响应遵循统一格式：\n\n```json\n{\n  \"error\": {\n    \"code\": \"invalid_parameter\",\n    \"message\": \"The \'email\' field must be a valid email address\",\n    \"details\": {\n      \"field\": \"email\",\n      \"reason\": \"format_invalid\"\n    },\n    \"request_id\": \"req_abc123\"\n  }\n}\n```\n\n常见错误码列表：\n\n| 错误码                  | 描述                   |\n|------------------------|------------------------|\n| `authentication_failed` | API Key 无效或过期      |\n| `permission_denied`     | 当前用户无操作权限      |\n| `resource_not_found`    | 请求的资源不存在        |\n| `duplicate_entry`       | 资源已存在（如邮箱重复） |\n| `validation_error`      | 请求体不符合校验规则    |\n| `rate_limit_exceeded`   | 请求频率超限            |\n\n---\n\n## 6. 示例代码（Python）\n\n以下使用 `requests` 库演示调用流程：\n\n```python\nimport requests\n\nBASE_URL = \"https://api.example.com/v2\"\nAPI_KEY = \"your_api_key\"\n\nheaders = {\n    \"X-API-Key\": API_KEY,\n    \"Content-Type\": \"application/json\"\n}\n\n# 获取用户列表\nresponse = requests.get(\n    f\"{BASE_URL}/users\",\n    headers=headers,\n    params={\"role\": \"admin\", \"limit\": 10}\n)\nif response.status_code == 200:\n    users = response.json()[\"data\"]\n    for user in users:\n        print(user[\"email\"])\nelse:\n    print(f\"Error: {response.status_code} - {response.text}\")\n\n# 创建新用户\nnew_user = {\n    \"email\": \"charlie@example.com\",\n    \"full_name\": \"Charlie Li\",\n    \"password\": \"Temp@your_password\",\n    \"role\": \"viewer\"\n}\nresp = requests.post(\n    f\"{BASE_URL}/users\",\n    json=new_user,\n    headers=headers\n)\nif resp.status_code == 201:\n    print(\"User created:\", resp.json()[\"id\"])\nelse:\n    error = resp.json().get(\"error\", {})\n    print(\"Failed:\", error.get(\"message\"))\n```\n\n---\n\n## 7. 版本控制\n\n当前主版本为 `v2`，次版本更新（新增字段、兼容性扩展）不会改变 URL 路径，仅通过响应头 `X-API-Version` 标记。\n\n破坏性变更将在新主版本（如 `v3`）发布，并至少提前 **3 个月** 通知开发者。\n\n---\n\n## 8. 常见问题（FAQ）\n\n- **Q：如何获取测试环境的 API Key？**  \n  A：联系内部开发者门户申请。\n\n- **Q：是否支持 GraphQL？**  \n  A：暂不支持，但后续版本可能考虑提供 GraphQL 封装层。\n\n- **Q：速率限制是多少？**  \n  A：默认 1000 次/分钟，可联系提升配额。\n\n---\n\n## 9. 变更日志\n\n| 日期       | 版本 | 说明 |\n|------------|------|------|\n| 2026-06-29 | 2.1.0 | 增加分页元数据，优化错误码 |\n| 2026-06-15 | 2.0.0 | 重构资源路径，废弃旧 `v1` 端点 |\n| 2026-05-01 | 1.2.0 | 初始公开版本 |\n\n---\n\n> **提示**：本文档是**测试 Markdown 示例**，所有数据均为虚构，仅用于展示格式语法。实际使用时请替换为真实内容。\n\n**反馈**：如有疑问或建议，请提交至 [GitHub Issues](https://github.com/example/api-docs/issues) 或发送邮件至 `api@example.com`。','https://your-cos-instance.com/lsky/2/2026/06/30/37017c914953348aa13534c081dff786_6a43e229b5fdb.jpg',1,2,0,0,0,2,'2026-06-29 10:48:43','2026-06-30 23:36:54','测试',0,NULL,NULL,0);
/*!40000 ALTER TABLE `busi_article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_article_tag`
--

DROP TABLE IF EXISTS `busi_article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_article_tag` (
  `article_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`article_id`,`tag_id`) USING BTREE,
  KEY `idx_tag` (`tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文章标签中间表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_article_tag`
--

LOCK TABLES `busi_article_tag` WRITE;
/*!40000 ALTER TABLE `busi_article_tag` DISABLE KEYS */;
INSERT INTO `busi_article_tag` VALUES (1,2),(4,2),(13,2),(14,6),(19,6),(3,7),(10,7),(15,7),(20,7),(10,9),(15,9),(20,9),(11,10),(17,10),(12,12),(18,12),(12,13),(18,13);
/*!40000 ALTER TABLE `busi_article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_category`
--

DROP TABLE IF EXISTS `busi_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` bigint DEFAULT '0' COMMENT '0=一级分类',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name_parent` (`name`,`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='专栏分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_category`
--

LOCK TABLES `busi_category` WRITE;
/*!40000 ALTER TABLE `busi_category` DISABLE KEYS */;
INSERT INTO `busi_category` VALUES (1,'前端开发',0,'2026-06-22 14:20:21'),(2,'后端开发',0,'2026-06-22 14:20:21'),(3,'云计算与运维',0,'2026-06-22 14:20:21'),(4,'人工智能与大数据',0,'2026-06-22 14:20:21'),(5,'信息安全',0,'2026-06-22 14:20:21'),(6,'软件架构',0,'2026-06-22 14:20:21');
/*!40000 ALTER TABLE `busi_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_comment`
--

DROP TABLE IF EXISTS `busi_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL COMMENT '所属文章ID',
  `user_id` bigint NOT NULL COMMENT '评论者ID',
  `parent_id` bigint DEFAULT '0' COMMENT '0=顶级评论 非0=回复某评论',
  `reply_to_user_id` bigint DEFAULT '0' COMMENT '被回复人ID(冗余)',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_deleted` tinyint DEFAULT '0' COMMENT '软删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_article` (`article_id`) USING BTREE,
  KEY `idx_parent` (`parent_id`) USING BTREE,
  KEY `idx_is_deleted` (`is_deleted`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_comment`
--

LOCK TABLES `busi_comment` WRITE;
/*!40000 ALTER TABLE `busi_comment` DISABLE KEYS */;
INSERT INTO `busi_comment` VALUES (1,1,1,0,0,'test comment','2026-06-22 11:50:44',0),(2,1,10,1,1,'awd','2026-06-22 13:47:20',0),(3,1,10,2,10,'vyguih','2026-06-22 14:01:16',0),(4,1,10,2,10,'huijoi\n','2026-06-22 14:01:37',0),(5,1,10,2,10,'wadawdaw','2026-06-22 14:02:18',0),(6,8,1,0,0,'L1','2026-06-22 14:05:28',0),(7,8,1,6,1,'L2','2026-06-22 14:05:28',0),(8,8,1,7,1,'L3','2026-06-22 14:05:28',0),(9,1,10,5,10,'qwdhawidhwu','2026-06-22 14:07:12',0),(10,1,10,9,10,'dwawadw','2026-06-22 14:07:17',0),(11,1,10,5,10,'dtfyfyu\n','2026-06-22 14:10:40',0),(12,9,1,0,0,'[L1] ????','2026-06-22 14:13:29',0),(13,9,1,12,1,'[L2] ??L1','2026-06-22 14:13:29',0),(14,9,1,13,1,'[L3] ??L2','2026-06-22 14:13:29',0),(15,9,1,14,1,'[L4] ??L3','2026-06-22 14:13:29',0),(16,9,1,0,0,'[L1] ?????','2026-06-22 14:13:29',0),(17,1,10,0,0,'sawdada','2026-06-22 14:24:28',0),(18,1,10,17,10,'etesresres','2026-06-22 14:24:49',0),(19,1,10,18,10,'rw3wr3srs3r33s','2026-06-22 14:24:53',0),(20,1,10,19,10,'s3e3rs3r3s','2026-06-22 14:24:58',0),(21,1,10,0,0,'szfxtgxdgxd','2026-06-22 14:25:37',0),(22,1,10,0,0,'zsfzsfzsfszfs','2026-06-22 14:25:41',0),(23,10,2,0,0,'写得很好，学习了！','2026-06-22 15:52:19',0),(24,10,3,0,0,'关注了，期待更多内容','2026-06-22 15:52:19',0),(25,12,1,0,0,'欢迎大家讨论交流','2026-06-22 15:52:19',0),(26,11,1,0,0,'索引优化是面试高频题，收藏了','2026-06-22 15:52:19',0),(27,15,2,0,0,'写得很详细，Spring Boot 3 确实简化了很多配置！','2026-06-22 16:53:06',0),(28,15,3,0,0,'自动配置的原理能详细讲讲吗？想深入了解底层实现','2026-06-22 16:53:06',0),(29,13,3,0,0,'Composition API 比 Options API 好用太多了，代码组织更清晰','2026-06-22 16:53:06',0),(30,16,3,0,0,'Composition API 比 Options API 好用太多了，代码组织更清晰','2026-06-22 16:53:06',0),(32,13,1,0,0,'ref 和 reactive 的选择有什么建议？','2026-06-22 16:53:06',0),(33,16,1,0,0,'ref 和 reactive 的选择有什么建议？','2026-06-22 16:53:06',0),(35,17,1,0,0,'索引优化是面试高频题，收藏了学习！','2026-06-22 16:53:06',0),(36,12,2,0,0,'Docker 部署讲得很清楚，已收藏','2026-06-22 16:53:06',0),(37,18,2,0,0,'Docker 部署讲得很清楚，已收藏','2026-06-22 16:53:06',0),(39,18,1,0,0,'建议加上 Docker Compose 多容器编排的部分','2026-06-22 16:53:06',0),(40,13,7,29,3,'cawwdw','2026-06-26 16:49:41',0),(41,13,7,40,7,'2eq2e2qe2q','2026-06-26 16:49:51',0),(42,13,7,40,7,'WADAWDAWDA','2026-06-26 18:00:30',0),(43,13,7,29,7,'挖到的','2026-06-26 18:03:07',0),(44,13,7,0,0,'大卫杜夫哇','2026-06-26 18:05:08',0);
/*!40000 ALTER TABLE `busi_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_favorite`
--

DROP TABLE IF EXISTS `busi_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_article` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='收藏记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_favorite`
--

LOCK TABLES `busi_favorite` WRITE;
/*!40000 ALTER TABLE `busi_favorite` DISABLE KEYS */;
INSERT INTO `busi_favorite` VALUES (1,1,12,'2026-06-26 16:36:43');
/*!40000 ALTER TABLE `busi_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_like`
--

DROP TABLE IF EXISTS `busi_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) USING BTREE,
  KEY `idx_article` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='点赞记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_like`
--

LOCK TABLES `busi_like` WRITE;
/*!40000 ALTER TABLE `busi_like` DISABLE KEYS */;
INSERT INTO `busi_like` VALUES (11,10,1,'2026-06-22 13:47:10'),(12,10,3,'2026-06-22 14:25:57'),(13,1,12,'2026-06-26 16:36:44');
/*!40000 ALTER TABLE `busi_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_search_history`
--

DROP TABLE IF EXISTS `busi_search_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_search_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `keyword` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '搜索关键词',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_user_keyword` (`user_id`,`keyword`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='搜索历史表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_search_history`
--

LOCK TABLES `busi_search_history` WRITE;
/*!40000 ALTER TABLE `busi_search_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `busi_search_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `busi_tag`
--

DROP TABLE IF EXISTS `busi_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `busi_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `busi_tag`
--

LOCK TABLES `busi_tag` WRITE;
/*!40000 ALTER TABLE `busi_tag` DISABLE KEYS */;
INSERT INTO `busi_tag` VALUES (1,'React','2026-06-22 10:46:17'),(2,'Vue.js','2026-06-22 10:46:17'),(3,'Node.js','2026-06-22 10:46:17'),(4,'GraphQL','2026-06-22 10:46:17'),(5,'Nginx','2026-06-22 10:46:17'),(6,'Python','2026-06-22 10:46:17'),(7,'Java','2026-06-22 10:46:17'),(8,'Go','2026-06-22 10:46:17'),(9,'Spring Boot','2026-06-22 10:46:17'),(10,'MySQL','2026-06-22 10:46:17'),(11,'Redis','2026-06-22 10:46:17'),(12,'Docker','2026-06-22 10:46:17'),(13,'Linux','2026-06-22 10:46:17'),(14,'Kafka','2026-06-22 10:46:17'),(15,'gRPC','2026-06-22 10:46:17'),(16,'OAuth2/JWT','2026-06-22 10:46:17'),(17,'Kubernetes','2026-06-22 10:46:17'),(18,'Terraform','2026-06-22 10:46:17'),(19,'PyTorch','2026-06-22 10:46:17'),(20,'TensorFlow','2026-06-22 10:46:17'),(21,'awswdfwa','2026-06-29 20:13:28');
/*!40000 ALTER TABLE `busi_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_media`
--

DROP TABLE IF EXISTS `sys_media`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_media` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '媒体ID',
  `original_name` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '原始文件名',
  `lsky_key` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Lsky 图片唯一密钥',
  `content_type` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'MIME类型',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小（字节）',
  `file_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图片访问URL（Lsky直链）',
  `thumbnail_url` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '缩略图URL',
  `image_width` int DEFAULT NULL COMMENT '图片宽度',
  `image_height` int DEFAULT NULL COMMENT '图片高度',
  `markdown` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Markdown格式链接（Lsky自动生成）',
  `file_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'article_image' COMMENT '文件分类：article_image/avatar/cover',
  `uploader_id` bigint DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_lsky_key` (`lsky_key`),
  KEY `idx_file_category` (`file_category`),
  KEY `idx_uploader_id` (`uploader_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体资源元数据表（Lsky Pro 图床）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_media`
--

LOCK TABLES `sys_media` WRITE;
/*!40000 ALTER TABLE `sys_media` DISABLE KEYS */;
INSERT INTO `sys_media` VALUES (1,'preview.jpg','mMFAU2','image/jpeg',558899,'https://your-cos-instance.com/lsky/2/2026/06/30/preview_6a43e22566981.jpg','https://your-cos-instance.com/lsky/2/2026/06/30/preview_6a43e22566981.jpg',NULL,NULL,'![preview.jpg](https://your-cos-instance.com/lsky/2/2026/06/30/preview_6a43e22566981.jpg)','article_image',1,'2026-06-22 15:53:55'),(2,'QQ_1782699621849.png','GlyT5m','image/png',40557,'https://your-cos-instance.com/lsky/2/2026/06/30/QQ_1782699621849_6a43e228339ec.png','https://your-lsky-instance.com/thumbnails/81214357032cbbda2af70ae59ef1f89e.png',NULL,NULL,'![QQ_1782699621849.png](https://your-cos-instance.com/lsky/2/2026/06/30/QQ_1782699621849_6a43e228339ec.png)','article_image',1,'2026-06-29 10:20:52'),(3,'37017c914953348aa13534c081dff786.jpg','gO3PVJ','image/jpeg',99162,'https://your-cos-instance.com/lsky/2/2026/06/30/37017c914953348aa13534c081dff786_6a43e229b5fdb.jpg','https://your-lsky-instance.com/thumbnails/ef60973ca0c609362f8a893ec27ffd55.png',NULL,NULL,'![37017c914953348aa13534c081dff786.jpg](https://your-cos-instance.com/lsky/2/2026/06/30/37017c914953348aa13534c081dff786_6a43e229b5fdb.jpg)','article_image',1,'2026-06-29 10:40:08'),(4,'image.png','WAjRay','image/png',205861,'https://your-cos-instance.com/lsky/2/2026/06/30/image_6a43e22c3ca48.png','https://your-lsky-instance.com/thumbnails/31d65d2c054ce50960f2c293622d3103.png',NULL,NULL,'![image.png](https://your-cos-instance.com/lsky/2/2026/06/30/image_6a43e22c3ca48.png)','article_image',1,'2026-06-29 20:14:34'),(5,'lofter_1691782945012.gif','uReJQ8','image/gif',1868398,'https://your-cos-instance.com/lsky/2/2026/05/27/lofter_1691782945012_6a16ab9176872.gif','https://your-lsky-instance.com/thumbnails/0a5a11f2a6f8e4f067335796c1a335d3.png',NULL,NULL,'![lofter_1691782945012.gif](https://your-cos-instance.com/lsky/2/2026/05/27/lofter_1691782945012_6a16ab9176872.gif)','article_image',1,'2026-06-29 20:15:02'),(6,'57724708dc7591017d.png','VYvnc0','image/png',923726,'https://your-cos-instance.com/lsky/2/2026/06/30/57724708dc7591017d_6a43e26d94508.png','https://your-lsky-instance.com/thumbnails/eb683dc77a41319814f7977d7cebd83f.png',NULL,NULL,'![57724708dc7591017d.png](https://your-cos-instance.com/lsky/2/2026/06/30/57724708dc7591017d_6a43e26d94508.png)','cover',1,'2026-06-30 23:36:14'),(7,'37017c914953348aa13534c081dff786.jpg','R3IiF0','image/jpeg',99162,'https://your-cos-instance.com/lsky/2/2026/06/30/37017c914953348aa13534c081dff786_6a43e229b5fdb.jpg','https://your-lsky-instance.com/thumbnails/ef60973ca0c609362f8a893ec27ffd55.png',NULL,NULL,'![37017c914953348aa13534c081dff786.jpg](https://your-cos-instance.com/lsky/2/2026/06/30/37017c914953348aa13534c081dff786_6a43e229b5fdb.jpg)','cover',1,'2026-06-30 23:36:45'),(8,'42a7882d78227f83a3679c3a1476e53f.jpg','tT1Ksu','image/jpeg',14638,'https://your-cos-instance.com/lsky/2/2026/06/30/42a7882d78227f83a3679c3a1476e53f_6a43e5f358f51.jpg','https://your-lsky-instance.com/thumbnails/42a7882d78227f83a3679c3a1476e53f.png',NULL,NULL,'![42a7882d78227f83a3679c3a1476e53f.jpg](https://your-cos-instance.com/lsky/2/2026/06/30/42a7882d78227f83a3679c3a1476e53f_6a43e5f358f51.jpg)','avatar',1,'2026-06-30 23:51:12');
/*!40000 ALTER TABLE `sys_media` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt加密',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
  `bio` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '个性签名',
  `skills` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '技术栈（逗号分隔）',
  `role` tinyint DEFAULT '0' COMMENT '0=普通用户 1=管理员',
  `is_recommended` tinyint DEFAULT '0' COMMENT '0=不推荐 1=推荐作者',
  `status` tinyint DEFAULT '1' COMMENT '0=封禁 1=正常',
  `ban_until` datetime DEFAULT NULL COMMENT '封禁截止时间(NULL=永久)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'admin','$2a$10$kIbXfZeC09FlghFF4BijHOMVNO5X3APu9naM482rp3VwlJgNoIjXi','admin@itblog.com','https://your-cos-instance.com/lsky/2/2026/06/30/42a7882d78227f83a3679c3a1476e53f_6a43e5f358f51.jpg','hello world','Spring Boot, Vue 3, TypeScript',1,1,1,NULL,'2026-06-22 10:44:07'),(5,'user123','$2a$10$dQDDCuMui74iJ0nP5WEXwegfTAtaEhevbjDSfteQhobCkgmIxFYK.','dasdscasf@qq.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 11:31:08'),(6,'testuser01','$2a$10$MMuKGaMqbWo.o1cgdYqTnuvRXbITXjaeia8OmUPQWkoGg3Nj86WCS','test01@test.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 11:42:35'),(7,'t1','$2a$10$dQDDCuMui74iJ0nP5WEXwegfTAtaEhevbjDSfteQhobCkgmIxFYK.','a@outlook.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 11:50:02'),(8,'finaltest','$2a$10$Rb/P157doLl8BcUBXslnM.5OYbtfy4AKumUaBcXn8RnmjbAoFf9WC','final@test.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 13:35:22'),(9,'browserTest','$2a$10$comk5W36ik8/62ZnvYuOi.KArr8xzY0LvSbQ/e9eI5yUDnDjohqWK','bt@test.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 13:38:28'),(10,'t2','$2a$10$JtvPCdXZPBM0x7Ynrx0cYe/qY4QS.3sBj/YH84m0JWNnvSekr4vzu','a',NULL,NULL,'',0,0,1,NULL,'2026-06-22 13:38:35'),(12,'张三','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','zhangsan@test.com',NULL,NULL,'',0,1,1,NULL,'2026-06-22 16:53:06'),(13,'李四','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','lisi@test.com',NULL,NULL,'',0,1,1,NULL,'2026-06-22 16:53:06'),(14,'王五','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','wangwu@test.com',NULL,NULL,'',0,1,1,NULL,'2026-06-22 16:53:06');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'demo_album'
--

--
-- Dumping routines for database 'demo_album'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-01  8:08:04
