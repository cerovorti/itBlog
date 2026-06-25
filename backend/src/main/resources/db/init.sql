-- ============================================================
-- IT技术博客平台 - 数据库初始化脚本
-- 数据库: MySQL 8.0
-- ============================================================

CREATE DATABASE IF NOT EXISTS demo_album
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE demo_album;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user (
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    username       VARCHAR(50)  NOT NULL UNIQUE,
    password       VARCHAR(255) NOT NULL COMMENT 'BCrypt加密',
    email          VARCHAR(100) NOT NULL,
    avatar         VARCHAR(255) COMMENT '头像URL',
    bio            VARCHAR(200) COMMENT '个性签名',
    skills         VARCHAR(500) DEFAULT '' COMMENT '技术栈（逗号分隔）',
    role           TINYINT DEFAULT 0 COMMENT '0=普通用户 1=管理员',
    is_recommended TINYINT DEFAULT 0 COMMENT '0=不推荐 1=推荐作者',
    status         TINYINT DEFAULT 1 COMMENT '0=封禁 1=正常',
    ban_until      DATETIME COMMENT '封禁截止时间(NULL=永久)',
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 专栏分类表（两级，parent_id=0为一级）
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_category (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL,
    parent_id   BIGINT DEFAULT 0 COMMENT '0=一级分类',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_name_parent (name, parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专栏分类表';

-- ============================================================
-- 3. 标签字典表
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_tag (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50) NOT NULL UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签字典表';

-- ============================================================
-- 4. 文章主表
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_article (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    title         VARCHAR(200) NOT NULL COMMENT '文章标题',
    content       LONGTEXT NOT NULL COMMENT '原始Markdown正文',
    summary       VARCHAR(500) NOT NULL DEFAULT '' COMMENT '文章摘要（手动填写）',
    cover_img     VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面图URL',
    author_id     BIGINT NOT NULL COMMENT '作者ID',
    category_id   BIGINT NOT NULL COMMENT '所属分类ID',
    view_count    INT DEFAULT 0 COMMENT '阅读量',
    like_count    INT DEFAULT 0 COMMENT '点赞量',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    status        TINYINT DEFAULT 0 COMMENT '0=草稿 1=已发布',
    is_deleted    TINYINT DEFAULT 0 COMMENT '0=正常 1=已删除（软删除）',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_author (author_id),
    INDEX idx_category (category_id),
    INDEX idx_status_create (status, create_time),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章主表';

-- ============================================================
-- 5. 文章-标签 中间表
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_article_tag (
    article_id  BIGINT NOT NULL,
    tag_id      BIGINT NOT NULL,
    PRIMARY KEY (article_id, tag_id),
    INDEX idx_tag (tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签中间表';

-- ============================================================
-- 6. 评论表（parent_id=0为顶级评论）
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_comment (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    article_id       BIGINT NOT NULL COMMENT '所属文章ID',
    user_id          BIGINT NOT NULL COMMENT '评论者ID',
    parent_id        BIGINT DEFAULT 0 COMMENT '0=顶级评论 非0=回复某评论',
    reply_to_user_id BIGINT DEFAULT 0 COMMENT '被回复人ID(冗余)',
    content          TEXT NOT NULL COMMENT '评论内容',
    is_deleted       TINYINT DEFAULT 0 COMMENT '0=正常 1=已删除（软删除）',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_article (article_id),
    INDEX idx_parent (parent_id),
    INDEX idx_is_deleted (is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================================
-- 7. 点赞记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_like (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    article_id  BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_article (user_id, article_id),
    INDEX idx_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- ============================================================
-- 8. 收藏记录表
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_favorite (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL,
    article_id  BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_article (user_id, article_id),
    INDEX idx_user (user_id),
    INDEX idx_article (article_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏记录表';

-- ============================================================
-- 9. 上传文件表（图片BLOB存储）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_file (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    filename      VARCHAR(255) NOT NULL COMMENT '存储文件名（UUID）',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    content_type  VARCHAR(100) NOT NULL COMMENT 'MIME类型',
    file_size     BIGINT NOT NULL COMMENT '文件大小（字节）',
    file_data     LONGBLOB NOT NULL COMMENT '文件二进制数据',
    uploader_id   BIGINT COMMENT '上传者ID',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_uploader (uploader_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上传文件表';

-- ============================================================
-- 10. 搜索历史表
-- ============================================================
CREATE TABLE IF NOT EXISTS busi_search_history (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    keyword     VARCHAR(200) NOT NULL COMMENT '搜索关键词',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_user_keyword (user_id, keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='搜索历史表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 管理员账号: admin / admin123
-- BCrypt hash of "admin123" (10 rounds)
INSERT IGNORE INTO sys_user (username, password, email, role) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'admin@itblog.com', 1);

-- 初始分类
INSERT IGNORE INTO busi_category (name, parent_id) VALUES
('前端开发', 0),
('后端开发', 0),
('数据库', 0),
('DevOps', 0),
('人工智能', 0),
('Vue.js', 1),
('React', 1),
('Java', 2),
('Python', 2),
('Go', 2),
('MySQL', 3),
('Redis', 3);

-- 初始标签
INSERT IGNORE INTO busi_tag (name) VALUES
('Spring Boot'), ('Vue 3'), ('TypeScript'), ('MyBatis-Plus'),
('JWT'), ('MySQL'), ('Docker'), ('Linux'), ('Git'), ('RESTful');
