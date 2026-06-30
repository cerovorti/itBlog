package com.itblog.service.impl;

import com.itblog.common.BusinessException;
import com.itblog.entity.Media;
import com.itblog.mapper.MediaMapper;
import com.itblog.service.FileService;
import com.itblog.utils.LskyUtil;
import com.itblog.utils.LskyUtil.LskyUploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 文件服务实现 —— 使用 Lsky Pro 图床替代 MinIO/BLOB。
 * 上传时调用 Lsky HTTP API，数据库仅保留元数据（url、key 等），
 * 迁移部署时只需 mysqldump 导入 SQL 即可，无需拷贝大体积 BLOB。
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp");

    private final LskyUtil lskyUtil;
    private final MediaMapper mediaMapper;
    private final RestTemplate restTemplate;

    public FileServiceImpl(LskyUtil lskyUtil, MediaMapper mediaMapper, RestTemplate restTemplate) {
        this.lskyUtil = lskyUtil;
        this.mediaMapper = mediaMapper;
        this.restTemplate = restTemplate;
    }

    @Override
    public Media uploadImage(MultipartFile file, Long uploaderId) {
        return doUpload(file, uploaderId, "article_image");
    }

    @Override
    public Media uploadAvatar(MultipartFile file, Long userId) {
        return doUpload(file, userId, "avatar");
    }

    @Override
    public Media uploadCover(MultipartFile file, Long userId) {
        return doUpload(file, userId, "cover");
    }

    @Override
    public Media getMedia(Long mediaId) {
        Media media = mediaMapper.selectById(mediaId);
        if (media == null) throw new BusinessException("文件不存在");
        return media;
    }

    @Override
    public String getFileUrl(Long mediaId) {
        Media media = getMedia(mediaId);
        return media.getFileUrl();
    }

    @Override
    public MediaWithData getFileData(Long mediaId) {
        Media media = getMedia(mediaId);
        // 从 Lsky URL 重新下载
        try {
            byte[] data = restTemplate.getForObject(media.getFileUrl(), byte[].class);
            if (data == null) throw new BusinessException("文件内容为空");
            return new MediaWithData(media.getContentType(), media.getOriginalName(),
                    media.getFileSize(), data);
        } catch (Exception e) {
            throw new BusinessException("读取文件失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(Long mediaId) {
        Media media = getMedia(mediaId);
        if (media.getLskyKey() != null) {
            lskyUtil.delete(media.getLskyKey());
        }
        mediaMapper.deleteById(mediaId);
    }

    // ========== 私有方法 ==========

    private Media doUpload(MultipartFile file, Long uploaderId, String category) {
        validateImage(file);
        try {
            String originalName = file.getOriginalFilename();
            String contentType = file.getContentType();
            byte[] bytes = file.getBytes();

            LskyUploadResult result = lskyUtil.upload(bytes, originalName);

            Media media = Media.builder()
                    .originalName(originalName)
                    .lskyKey(result.key)
                    .contentType(contentType)
                    .fileSize(file.getSize())
                    .fileUrl(result.url)
                    .thumbnailUrl(result.thumbnailUrl)
                    .imageWidth(result.width)
                    .imageHeight(result.height)
                    .markdown(result.markdown)
                    .fileCategory(category)
                    .uploaderId(uploaderId)
                    .createTime(LocalDateTime.now())
                    .build();
            mediaMapper.insert(media);

            return media;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) throw new BusinessException("文件为空");
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持 jpg/png/gif/webp/bmp 格式");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BusinessException("文件大小不能超过 10MB");
        }
    }
}