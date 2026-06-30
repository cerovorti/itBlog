package com.itblog.service;

import com.itblog.entity.Media;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口 —— 将图片存储到 Lsky Pro 图床
 */
public interface FileService {

    /** 上传图片（文章内容图片） */
    Media uploadImage(MultipartFile file, Long uploaderId);

    /** 上传头像 */
    Media uploadAvatar(MultipartFile file, Long userId);

    /** 上传文章封面图 */
    Media uploadCover(MultipartFile file, Long userId);

    /** 按 ID 获取媒体信息 */
    Media getMedia(Long mediaId);

    /** 获取文件访问 URL */
    String getFileUrl(Long mediaId);

    /** 直接按 ID 返回文件字节流（兼容旧接口 /api/file/image/{id}，从 Lsky 重新下载） */
    MediaWithData getFileData(Long mediaId);

    /** 删除文件 */
    void deleteFile(Long mediaId);

    /** 封装：文件元数据 + 字节数据 */
    class MediaWithData {
        private final String contentType;
        private final String originalName;
        private final long fileSize;
        private final byte[] data;

        public MediaWithData(String contentType, String originalName, long fileSize, byte[] data) {
            this.contentType = contentType;
            this.originalName = originalName;
            this.fileSize = fileSize;
            this.data = data;
        }
        public String getContentType() { return contentType; }
        public String getOriginalName() { return originalName; }
        public long getFileSize() { return fileSize; }
        public byte[] getData() { return data; }
    }
}