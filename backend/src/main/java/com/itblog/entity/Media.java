package com.itblog.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 媒体资源实体 —— 记录 Lsky Pro 图床中的图片元数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_media")
public class Media {

    /** 媒体ID（自增主键） */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 原始文件名 */
    private String originalName;

    /** Lsky 图片唯一密钥 */
    private String lskyKey;

    /** MIME 类型 */
    private String contentType;

    /** 文件大小（字节） */
    private Long fileSize;

    /** 图片访问URL */
    private String fileUrl;

    /** 缩略图URL */
    private String thumbnailUrl;

    /** 图片宽度 */
    private Integer imageWidth;

    /** 图片高度 */
    private Integer imageHeight;

    /** Markdown 格式链接（Lsky 自动生成） */
    private String markdown;

    /** 文件分类：article_image / avatar / cover */
    private String fileCategory;

    /** 上传用户ID */
    private Long uploaderId;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}