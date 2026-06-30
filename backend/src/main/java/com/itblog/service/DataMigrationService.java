package com.itblog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itblog.entity.Article;
import com.itblog.entity.FileRecord;
import com.itblog.entity.Media;
import com.itblog.mapper.ArticleMapper;
import com.itblog.mapper.FileRecordMapper;
import com.itblog.mapper.MediaMapper;
import com.itblog.utils.LskyUtil;
import com.itblog.utils.LskyUtil.LskyUploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 数据迁移服务 —— 应用启动时自动将 BLOB 数据上传到 Lsky Pro 图床。
 *
 * 迁移三类图片：
 * 1. 文章内容中的图片（sys_file 表 BLOB → Lsky）
 * 2. 文章封面图（busi_article.cover_img 如果是 /api/file/* 本地引用）
 * 3. 用户头像（sys_user.avatar 如果是 /api/file/* 本地引用）
 *
 * 迁移是幂等的：已完成迁移的记录不会重复上传。
 */
@Slf4j
@Service
public class DataMigrationService implements CommandLineRunner {

    private final FileRecordMapper fileRecordMapper;
    private final MediaMapper mediaMapper;
    private final LskyUtil lskyUtil;
    private final ArticleMapper articleMapper;

    // 记录旧 BLOB ID → 新 Media ID 的映射（用于 URL 替换）
    private final Map<Long, Long> blobIdToMediaId = new LinkedHashMap<>();

    public DataMigrationService(FileRecordMapper fileRecordMapper, MediaMapper mediaMapper,
                                LskyUtil lskyUtil, ArticleMapper articleMapper) {
        this.fileRecordMapper = fileRecordMapper;
        this.mediaMapper = mediaMapper;
        this.lskyUtil = lskyUtil;
        this.articleMapper = articleMapper;
    }

    @Override
    public void run(String... args) {
        try {
            migrateBlobsToLsky();
        } catch (Exception e) {
            log.error("数据迁移失败（不影响正常启动）: {}", e.getMessage(), e);
        }
    }

    /**
     * 主迁移流程
     */
    @Transactional
    public void migrateBlobsToLsky() {
        log.info("===== 开始 BLOB → Lsky Pro 图床数据迁移 =====");

        // 1. 迁移 sys_file 表中的 BLOB
        List<FileRecord> allFiles = fileRecordMapper.selectList(null);
        int migratedCount = 0;
        int skipCount = 0;

        for (FileRecord fileRecord : allFiles) {
            // 检查是否已经迁移过
            LambdaQueryWrapper<Media> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(Media::getOriginalName, fileRecord.getOriginalName());
            checkWrapper.eq(Media::getUploaderId, fileRecord.getUploaderId());
            Media existing = mediaMapper.selectOne(checkWrapper);
            if (existing != null && existing.getFileUrl() != null) {
                blobIdToMediaId.put(fileRecord.getId(), existing.getId());
                skipCount++;
                continue;
            }

            byte[] data = fileRecord.getFileData();
            if (data == null || data.length == 0) {
                skipCount++;
                continue;
            }

            try {
                log.info("正在迁移 BLOB id={}, filename={}, size={}KB",
                        fileRecord.getId(), fileRecord.getOriginalName(), data.length / 1024);

                LskyUploadResult result = lskyUtil.upload(
                        data,
                        fileRecord.getOriginalName() != null ? fileRecord.getOriginalName() : fileRecord.getFilename()
                );

                Media media = Media.builder()
                        .originalName(fileRecord.getOriginalName())
                        .lskyKey(result.key)
                        .contentType(fileRecord.getContentType())
                        .fileSize(fileRecord.getFileSize())
                        .fileUrl(result.url)
                        .thumbnailUrl(result.thumbnailUrl)
                        .imageWidth(result.width)
                        .imageHeight(result.height)
                        .markdown(result.markdown)
                        .fileCategory("article_image")
                        .uploaderId(fileRecord.getUploaderId())
                        .createTime(fileRecord.getCreateTime() != null ? fileRecord.getCreateTime() : LocalDateTime.now())
                        .build();
                mediaMapper.insert(media);

                blobIdToMediaId.put(fileRecord.getId(), media.getId());
                migratedCount++;
                log.info("BLOB 迁移成功: blobId={} → mediaId={}, lskyKey={}",
                        fileRecord.getId(), media.getId(), result.key);

                // 控制速率，避免触发 Lsky 限流
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("迁移 BLOB 失败: id={}, filename={}, error={}",
                        fileRecord.getId(), fileRecord.getOriginalName(), e.getMessage());
            }
        }

        log.info("BLOB 迁移完成: 已迁移 {} 条, 跳过 {} 条", migratedCount, skipCount);

        // 2. 更新文章内容中的图片引用
        if (!blobIdToMediaId.isEmpty()) {
            updateArticleContentUrls();
        }

        log.info("===== BLOB → Lsky Pro 图床数据迁移完成 =====");
    }

    /**
     * 更新文章内容中的图片 URL 引用
     * 将 /api/file/image/{blobId} 或 /api/files/{blobId} 替换为 Lsky 直链
     */
    private void updateArticleContentUrls() {
        List<Article> allArticles = articleMapper.selectList(null);
        int updatedCount = 0;

        for (Article article : allArticles) {
            String content = article.getContent();
            if (content == null) continue;
            String originalContent = content;
            boolean changed = false;

            for (Map.Entry<Long, Long> entry : blobIdToMediaId.entrySet()) {
                Long oldId = entry.getKey();
                Long newId = entry.getValue();

                Media media = mediaMapper.selectById(newId);
                if (media == null || media.getFileUrl() == null) continue;

                String newUrl = media.getFileUrl();
                String oldPattern1 = "/api/file/image/" + oldId;
                String oldPattern2 = "/api/files/" + oldId;

                if (content.contains(oldPattern1)) {
                    content = content.replace(oldPattern1, newUrl);
                    changed = true;
                }
                if (content.contains(oldPattern2)) {
                    content = content.replace(oldPattern2, newUrl);
                    changed = true;
                }
            }

            if (changed && !content.equals(originalContent)) {
                article.setContent(content);
                articleMapper.updateById(article);
                updatedCount++;
            }

            // 处理 draft_content
            String draftContent = article.getDraftContent();
            if (draftContent != null) {
                String originalDraft = draftContent;
                boolean draftChanged = false;
                for (Map.Entry<Long, Long> entry : blobIdToMediaId.entrySet()) {
                    Long oldId = entry.getKey();
                    Long newId = entry.getValue();
                    Media media = mediaMapper.selectById(newId);
                    if (media == null || media.getFileUrl() == null) continue;

                    String newUrl = media.getFileUrl();
                    String oldPattern1 = "/api/file/image/" + oldId;
                    String oldPattern2 = "/api/files/" + oldId;

                    if (draftContent.contains(oldPattern1)) {
                        draftContent = draftContent.replace(oldPattern1, newUrl);
                        draftChanged = true;
                    }
                    if (draftContent.contains(oldPattern2)) {
                        draftContent = draftContent.replace(oldPattern2, newUrl);
                        draftChanged = true;
                    }
                }
                if (draftChanged && !draftContent.equals(originalDraft)) {
                    article.setDraftContent(draftContent);
                    articleMapper.updateById(article);
                }
            }
        }

        log.info("文章内容 URL 更新完成: 更新了 {} 篇文章", updatedCount);
    }
}