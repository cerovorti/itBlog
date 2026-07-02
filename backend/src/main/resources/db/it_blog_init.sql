-- ============================================================
-- 初始化脚本: it_blog
-- 生成时间: 2026-07-01 14:34:50
-- 说明: 本文件由 db_export_init.py 自动生成，用于项目初始化
--       数据已做脱敏处理，仅保留示例数据
-- ============================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 建库（如已存在则忽略）
CREATE DATABASE IF NOT EXISTS `it_blog`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `it_blog`;

-- ------------------------------------------------------------
-- 表结构
-- ------------------------------------------------------------

--
-- Table structure for table `busi_article`
--

CREATE TABLE `busi_article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '原始Markdown正文',
  `cover_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '封面图URL',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `category_id` bigint DEFAULT NULL COMMENT '所属分类ID（草稿可选）',
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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文章主表';

--
-- Table structure for table `busi_article_tag`
--

CREATE TABLE `busi_article_tag` (
  `article_id` bigint NOT NULL,
  `tag_id` bigint NOT NULL,
  PRIMARY KEY (`article_id`,`tag_id`) USING BTREE,
  KEY `idx_tag` (`tag_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='文章标签中间表';

--
-- Table structure for table `busi_category`
--

CREATE TABLE `busi_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_id` bigint DEFAULT '0' COMMENT '0=一级分类',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name_parent` (`name`,`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='专栏分类表';

--
-- Table structure for table `busi_comment`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='评论表';

--
-- Table structure for table `busi_favorite`
--

CREATE TABLE `busi_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_article` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='收藏记录表';

--
-- Table structure for table `busi_like`
--

CREATE TABLE `busi_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `article_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_article` (`user_id`,`article_id`) USING BTREE,
  KEY `idx_article` (`article_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='点赞记录表';

--
-- Table structure for table `busi_search_history`
--

CREATE TABLE `busi_search_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `keyword` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '搜索关键词',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user` (`user_id`) USING BTREE,
  KEY `idx_user_keyword` (`user_id`,`keyword`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC COMMENT='搜索历史表';

--
-- Table structure for table `busi_tag`
--

CREATE TABLE `busi_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='标签字典表';

--
-- Table structure for table `sys_media`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='媒体资源元数据表（Lsky Pro 图床）';

--
-- Table structure for table `sys_user`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='用户表';

-- ------------------------------------------------------------
-- 初始数据（已脱敏，仅保留示例数据）
-- ------------------------------------------------------------

--
-- Dumping data for table `busi_article`
--

LOCK TABLES `busi_article` WRITE;
INSERT INTO `busi_article` (`id`, `title`, `content`, `cover_img`, `author_id`, `category_id`, `view_count`, `like_count`, `comment_count`, `status`, `create_time`, `update_time`, `summary`, `is_deleted`, `draft_content`, `draft_summary`, `review_status`) VALUES
(1,'Spring Boot 3 快速入门指南','# Spring Boot 3 快速入门\n\nSpring Boot 是快速开发脚手架。\n\n## 核心特性\n- 自动配置：根据依赖自动配置 Bean\n- 起步依赖：一站式引入常用组件\n- 内嵌服务器：无需部署 Tomcat\n\n## 快速开始\n```java\n@SpringBootApplication\npublic class DemoApplication {\n    public static void main(String[] args) {\n        SpringApplication.run(DemoApplication.class, args);\n    }\n}\n```\n\n只需一个注解即可启动应用。','https://picsum.photos/seed/spring/800/400',1,2,130,15,2,1,'2026-06-22 15:51:23','2026-06-26 20:33:32','面向初学者的 Spring Boot 3 入门教程，涵盖核心特性和快速上手',0,NULL,NULL,0),
(2,'Vue 3 Composition API 实战','# Vue 3 Composition API 实战\n\nComposition API 是 Vue 3 最重要的新特性。\n\n## setup 语法糖\n```vue\n<script setup>\nimport { ref, computed } from ''vue''\nconst count = ref(0)\nconst double = computed(() => count.value * 2)\n</script>\n```\n\n## 优势\n- 更好的逻辑复用\n- 更清晰的代码组织\n- 完整的 TypeScript 支持','https://picsum.photos/seed/vue/800/400',1,1,256,28,2,1,'2026-07-01 14:21:39','2026-07-01 14:21:14','分享 Vue 3 Composition API 的实用技巧和最佳实践',0,NULL,NULL,0),
(3,'Docker 容器化部署全流程','# Docker 容器化部署\n\n使用 Docker 可以告别"在我机器上能跑"的问题。\n\n## Dockerfile 示例\n```dockerfile\nFROM openjdk:17\nCOPY target/app.jar app.jar\nENTRYPOINT ["java", "-jar", "/app.jar"]\n```\n\n## Docker Compose\n```yaml\nversion: "3"\nservices:\n  app:\n    build: .\n    ports:\n      - "8080:8080"\n```','https://picsum.photos/seed/docker/800/400',1,3,198,22,2,1,'2026-06-22 16:53:06','2026-06-26 22:57:06','从零开始学习 Docker 容器化部署的完整流程',0,NULL,NULL,0);
UNLOCK TABLES;

--
-- Dumping data for table `busi_article_tag`
--

LOCK TABLES `busi_article_tag` WRITE;
INSERT INTO `busi_article_tag` (`article_id`, `tag_id`) VALUES (1,7),(1,9),(2,2),(3,12),(3,13);
UNLOCK TABLES;

--
-- Dumping data for table `busi_category`
--

LOCK TABLES `busi_category` WRITE;
INSERT INTO `busi_category` (`id`, `name`, `parent_id`, `create_time`) VALUES (1,'前端开发',0,'2026-06-22 14:20:21'),(2,'后端开发',0,'2026-06-22 14:20:21'),(3,'云计算与运维',0,'2026-06-22 14:20:21'),(4,'人工智能与大数据',0,'2026-06-22 14:20:21'),(5,'信息安全',0,'2026-06-22 14:20:21'),(6,'软件架构',0,'2026-06-22 14:20:21');
UNLOCK TABLES;

--
-- Dumping data for table `busi_comment`
--

LOCK TABLES `busi_comment` WRITE;
INSERT INTO `busi_comment` (`id`, `article_id`, `user_id`, `parent_id`, `reply_to_user_id`, `content`, `create_time`, `is_deleted`) VALUES
(1,1,5,0,0,'写得很好，学习了！','2026-06-22 15:52:19',0),
(2,1,6,0,0,'Spring Boot 3 确实简化了很多配置！','2026-06-22 16:53:06',0),
(3,2,6,0,0,'Composition API 比 Options API 好用太多了，代码组织更清晰','2026-06-22 16:53:06',0),
(4,3,5,0,0,'Docker 部署讲得很清楚，已收藏','2026-06-22 16:53:06',0);
UNLOCK TABLES;

--
-- Dumping data for table `busi_favorite`
--

LOCK TABLES `busi_favorite` WRITE;
UNLOCK TABLES;

--
-- Dumping data for table `busi_like`
--

LOCK TABLES `busi_like` WRITE;
UNLOCK TABLES;

--
-- Dumping data for table `busi_search_history`
--

LOCK TABLES `busi_search_history` WRITE;
UNLOCK TABLES;

--
-- Dumping data for table `busi_tag`
--

LOCK TABLES `busi_tag` WRITE;
INSERT INTO `busi_tag` (`id`, `name`, `create_time`) VALUES (1,'React','2026-06-22 10:46:17'),(2,'Vue.js','2026-06-22 10:46:17'),(3,'Node.js','2026-06-22 10:46:17'),(4,'GraphQL','2026-06-22 10:46:17'),(5,'Nginx','2026-06-22 10:46:17'),(6,'Python','2026-06-22 10:46:17'),(7,'Java','2026-06-22 10:46:17'),(8,'Go','2026-06-22 10:46:17'),(9,'Spring Boot','2026-06-22 10:46:17'),(10,'MySQL','2026-06-22 10:46:17'),(11,'Redis','2026-06-22 10:46:17'),(12,'Docker','2026-06-22 10:46:17'),(13,'Linux','2026-06-22 10:46:17'),(14,'Kafka','2026-06-22 10:46:17'),(15,'gRPC','2026-06-22 10:46:17'),(16,'OAuth2/JWT','2026-06-22 10:46:17'),(17,'Kubernetes','2026-06-22 10:46:17'),(18,'Terraform','2026-06-22 10:46:17'),(19,'PyTorch','2026-06-22 10:46:17'),(20,'TensorFlow','2026-06-22 10:46:17');
UNLOCK TABLES;

--
-- Dumping data for table `sys_media`
--

LOCK TABLES `sys_media` WRITE;
UNLOCK TABLES;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `avatar`, `bio`, `skills`, `role`, `is_recommended`, `status`, `ban_until`, `create_time`) VALUES
(1,'admin','$2b$10$k7gyjvQdVzZmV0ME70Ih8e/NGAPJMZ5PiqsFzA3AKAuJ/bCTV0ymi','admin@example.com','https://example.com/avatar/placeholder.jpg','hello world','Spring Boot, Vue 3, TypeScript',1,1,1,NULL,'2026-06-22 10:44:07'),
(5,'test_user_1','$2a$10$placeholder_hash_for_dev_use_only','user1@example.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 11:31:08'),
(6,'test_user_2','$2a$10$placeholder_hash_for_dev_use_only','user2@example.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 11:42:35'),
(7,'test_user_3','$2a$10$placeholder_hash_for_dev_use_only','user3@example.com',NULL,NULL,'',0,0,1,NULL,'2026-06-22 11:50:02');
UNLOCK TABLES;

SET FOREIGN_KEY_CHECKS = 1;