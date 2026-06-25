package com.itblog.service.impl;

import com.itblog.common.BusinessException;
import com.itblog.entity.FileRecord;
import com.itblog.mapper.FileRecordMapper;
import com.itblog.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * 图片服务实现 —— 图片仅存入数据库 BLOB，不写磁盘。
 * 迁移部署时只需导出/导入 MySQL，无需拷贝文件目录。
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final FileRecordMapper fileRecordMapper;

    public FileServiceImpl(FileRecordMapper fileRecordMapper) {
        this.fileRecordMapper = fileRecordMapper;
    }

    @Override
    public FileRecord uploadImage(MultipartFile file, Long uploaderId) {
        if (file.isEmpty()) throw new BusinessException("文件为空");
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new BusinessException("仅支持 jpg/png/gif/webp 格式");
        }

        try {
            String originalName = file.getOriginalFilename();
            String ext = ".png";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + ext;
            byte[] data = file.getBytes();

            FileRecord record = new FileRecord();
            record.setFilename(filename);
            record.setOriginalName(originalName != null ? originalName : filename);
            record.setContentType(contentType);
            record.setFileSize(file.getSize());
            record.setFileData(data);
            record.setUploaderId(uploaderId);
            fileRecordMapper.insert(record);

            return record;
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public FileRecord getFile(Long fileId) {
        FileRecord record = fileRecordMapper.selectById(fileId);
        if (record == null) {
            throw new BusinessException("文件不存在");
        }
        return record;
    }
}
